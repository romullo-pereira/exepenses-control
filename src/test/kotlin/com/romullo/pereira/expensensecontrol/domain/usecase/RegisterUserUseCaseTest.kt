package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.application.usecase.RegisterUserUseCaseImpl
import com.romullo.pereira.expensensecontrol.domain.exception.DuplicateEmailException
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidInputException
import com.romullo.pereira.expensensecontrol.domain.model.user.RegisterRequest
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

// In-memory fake implementation of UserRepositoryPort for stateful property tests
class InMemoryUserRepository : UserRepositoryPort {
    private val users = mutableListOf<User>()

    fun clear() = users.clear()
    fun count() = users.size

    override fun save(user: User): User {
        users.add(user)
        return user
    }

    override fun findByEmail(email: String): User? = users.find { it.email == email }

    override fun existsByEmail(email: String): Boolean = users.any { it.email == email }
}

// Feature: personal-expense-control, Property 1: Registro armazena senha como hash bcrypt válido
class RegisterUserUseCaseTest : StringSpec({

    val passwordEncoder = BCryptPasswordEncoder(10)
    val userRepository = mockk<UserRepositoryPort>()
    val useCase = RegisterUserUseCaseImpl(userRepository, passwordEncoder)

    // Generates valid emails like "abc@domain.com"
    val arbEmail: Arb<String> = arbitrary {
        val local = Arb.string(minSize = 1, maxSize = 10)
            .filter { s -> s.isNotBlank() && s.all { c -> c.isLetterOrDigit() } }
            .bind()
        val domain = Arb.string(minSize = 2, maxSize = 8)
            .filter { s -> s.isNotBlank() && s.all { c -> c.isLetterOrDigit() } }
            .bind()
        "$local@$domain.com"
    }

    // Generates valid passwords with at least 8 characters
    val arbPassword: Arb<String> = Arb.string(minSize = 8, maxSize = 64).filter { it.isNotBlank() }

    // Feature: personal-expense-control, Property 1: Registro armazena senha como hash bcrypt válido
    // Validates: Requirements 1.1, 1.4, 9.4
    "para qualquer email e senha validos, a senha armazenada deve ser um hash bcrypt valido e diferente da senha original" {
        val savedUserSlot = slot<User>()

        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(capture(savedUserSlot)) } answers { savedUserSlot.captured }

        checkAll(100, arbEmail, arbPassword) { email, password ->
            val request = RegisterRequest(email = email, password = password)
            useCase.register(request)

            val storedHash = savedUserSlot.captured.passwordHash

            // Hash must start with BCrypt prefix ($2a$ or $2b$)
            val isBcryptHash = storedHash.startsWith("\$2a\$") || storedHash.startsWith("\$2b\$")
            isBcryptHash shouldBe true

            // Hash must not equal the original password
            storedHash shouldNotBe password

            // BCrypt hash must verify correctly against the original password
            passwordEncoder.matches(password, storedHash) shouldBe true
        }
    }

    // Feature: personal-expense-control, Property 2: E-mail duplicado causa conflito
    // Validates: Requirements 1.2, 10.3
    "para qualquer email ja registrado, uma segunda tentativa de registro deve lancar DuplicateEmailException e nao aumentar o numero de usuarios" {
        val inMemoryRepo = InMemoryUserRepository()
        val useCaseWithRealRepo = RegisterUserUseCaseImpl(inMemoryRepo, passwordEncoder)

        checkAll(100, arbEmail, arbPassword) { email, password ->
            // Reset state for each iteration
            inMemoryRepo.clear()

            // First registration must succeed
            val firstRequest = RegisterRequest(email = email, password = password)
            useCaseWithRealRepo.register(firstRequest)
            val countAfterFirst = inMemoryRepo.count()
            countAfterFirst shouldBe 1

            // Second registration with the same email must throw DuplicateEmailException
            val secondRequest = RegisterRequest(email = email, password = password)
            shouldThrow<DuplicateEmailException> {
                useCaseWithRealRepo.register(secondRequest)
            }

            // Number of users must not increase after the duplicate attempt
            inMemoryRepo.count() shouldBe countAfterFirst
        }
    }

    // Feature: personal-expense-control, Property 3: Validação de entrada no registro
    // Validates: Requirements 1.3
    "para qualquer email invalido ou senha com menos de 8 caracteres, o registro deve ser rejeitado com InvalidInputException e nenhum usuario deve ser persistido" {
        val inMemoryRepo = InMemoryUserRepository()
        val useCaseWithRealRepo = RegisterUserUseCaseImpl(inMemoryRepo, passwordEncoder)

        // Generator for invalid emails: strings without '@' or without domain after '@'
        val arbInvalidEmail: Arb<String> = Arb.string(minSize = 1, maxSize = 30)
            .filter { s ->
                s.isNotBlank() && (
                    !s.contains('@') ||                          // missing '@'
                    s.endsWith('@') ||                           // no domain after '@'
                    s.substringAfter('@').isBlank() ||           // empty domain
                    !s.substringAfter('@').contains('.')         // no dot in domain
                )
            }

        // Generator for short passwords (0 to 7 characters)
        val arbShortPassword: Arb<String> = Arb.string(minSize = 0, maxSize = 7)

        // Test invalid emails with valid passwords
        checkAll(50, arbInvalidEmail, arbPassword) { invalidEmail, validPassword ->
            inMemoryRepo.clear()
            shouldThrow<InvalidInputException> {
                useCaseWithRealRepo.register(RegisterRequest(email = invalidEmail, password = validPassword))
            }
            inMemoryRepo.count() shouldBe 0
        }

        // Test valid emails with short passwords
        checkAll(50, arbEmail, arbShortPassword) { validEmail, shortPassword ->
            inMemoryRepo.clear()
            shouldThrow<InvalidInputException> {
                useCaseWithRealRepo.register(RegisterRequest(email = validEmail, password = shortPassword))
            }
            inMemoryRepo.count() shouldBe 0
        }
    }
})
