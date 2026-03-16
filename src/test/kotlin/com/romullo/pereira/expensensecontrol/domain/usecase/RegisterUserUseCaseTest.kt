package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.application.usecase.RegisterUserUseCaseImpl
import com.romullo.pereira.expensensecontrol.domain.model.user.RegisterRequest
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

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
})
