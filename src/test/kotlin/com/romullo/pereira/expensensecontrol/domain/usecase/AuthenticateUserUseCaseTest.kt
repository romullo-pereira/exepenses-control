package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security.JwtUtil
import com.romullo.pereira.expensensecontrol.application.usecase.AuthenticateUserUseCaseImpl
import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidCredentialsException
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Date

// Feature: personal-expense-control, Property 4: Login retorna JWT com userId e expiração correta
class AuthenticateUserUseCaseTest : StringSpec({

    // Use a fixed 256-bit (32-byte) secret key for tests
    val testSecretKey = "test-secret-key-for-property-tests-32b!"
    // 24h in milliseconds
    val expirationTimeMs = 24L * 60L * 60L * 1000L

    val jwtUtil = JwtUtil(
        expirationTime = expirationTimeMs,
        secretKeyString = testSecretKey,
    )

    val passwordEncoder = BCryptPasswordEncoder(10)
    val userRepository = mockk<UserRepositoryPort>()

    val useCase = AuthenticateUserUseCaseImpl(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        jwtUtil = jwtUtil,
        expirationTime = expirationTimeMs,
    )

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
    val arbPassword: Arb<String> = Arb.string(minSize = 8, maxSize = 64)
        .filter { it.isNotBlank() }

    // Generates valid user IDs (non-blank strings)
    val arbUserId: Arb<String> = Arb.string(minSize = 1, maxSize = 24)
        .filter { it.isNotBlank() && it.all { c -> c.isLetterOrDigit() } }

    // Feature: personal-expense-control, Property 4: Login retorna JWT com userId e expiração correta
    // Validates: Requirements 2.1, 2.3
    "para qualquer usuario registrado, o JWT retornado deve conter o userId correto e expiracao entre 23h55min e 24h05min" {
        checkAll(100, arbEmail, arbPassword, arbUserId) { email, rawPassword, userId ->
            val hashedPassword = passwordEncoder.encode(rawPassword)
            val user = User(
                id = userId,
                email = email,
                passwordHash = hashedPassword,
            )

            every { userRepository.findByEmail(email) } returns user

            val beforeCall = System.currentTimeMillis()
            val tokenResponse = useCase.authenticate(LoginRequest(email = email, password = rawPassword))
            val afterCall = System.currentTimeMillis()

            val token = tokenResponse.token

            // (a) Token must be decodable with the configured secret key
            val signingKey = Keys.hmacShaKeyFor(testSecretKey.toByteArray())
            val claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body

            // (b) Token payload must contain the correct userId
            val tokenUserId = claims["userId"].toString()
            tokenUserId shouldBe userId

            // (c) Expiration must be between 23h55min and 24h05min from generation time
            val expiration: Date = claims.expiration
            val expirationMs = expiration.time

            val minExpiration = beforeCall + (23L * 60L + 55L) * 60L * 1000L  // 23h55min
            val maxExpiration = afterCall  + (24L * 60L +  5L) * 60L * 1000L  // 24h05min

            expirationMs.shouldBeBetween(minExpiration, maxExpiration)
        }
    }

    // Feature: personal-expense-control, Property 5: Login com credenciais inválidas é rejeitado
    // Validates: Requirements 2.2
    "para qualquer combinacao de email e senha que nao corresponda a um usuario registrado, deve lancar InvalidCredentialsException" {
        checkAll(100, arbEmail, arbPassword, arbPassword) { email, registeredPassword, attemptedPassword ->
            // Case 1: non-existent email — repository returns null
            every { userRepository.findByEmail(email) } returns null

            shouldThrow<InvalidCredentialsException> {
                useCase.authenticate(LoginRequest(email = email, password = attemptedPassword))
            }

            // Case 2: existing user but wrong password (ensure passwords differ)
            val differentPassword = if (registeredPassword == attemptedPassword) attemptedPassword + "X" else attemptedPassword
            val hashedPassword = passwordEncoder.encode(registeredPassword)
            val userId = "user-${email.hashCode()}"
            val user = User(id = userId, email = email, passwordHash = hashedPassword)

            every { userRepository.findByEmail(email) } returns user

            shouldThrow<InvalidCredentialsException> {
                useCase.authenticate(LoginRequest(email = email, password = differentPassword))
            }
        }
    }
})
