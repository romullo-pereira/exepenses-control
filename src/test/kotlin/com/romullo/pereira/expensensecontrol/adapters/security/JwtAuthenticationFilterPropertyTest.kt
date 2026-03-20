package com.romullo.pereira.expensensecontrol.adapters.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security.JwtAuthenticationFilter
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security.JwtUtil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

// Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
class JwtAuthenticationFilterPropertyTest : StringSpec({

    val testSecretKey = "test-secret-key-for-property-tests-32b!"
    val expirationTimeMs = 24L * 60L * 60L * 1000L
    val signingKey = Keys.hmacShaKeyFor(testSecretKey.toByteArray())

    val jwtUtil = JwtUtil(
        expirationTime = expirationTimeMs,
        secretKeyString = testSecretKey,
    )
    val objectMapper = ObjectMapper().findAndRegisterModules()
    val filter = JwtAuthenticationFilter(jwtUtil, objectMapper)

    // Generates valid user IDs (alphanumeric, non-blank)
    val arbUserId: Arb<String> = Arb.string(minSize = 1, maxSize = 24)
        .filter { it.isNotBlank() && it.all { c -> c.isLetterOrDigit() } }

    // Generates valid emails
    val arbEmail: Arb<String> = arbitrary {
        val local = Arb.string(minSize = 1, maxSize = 10)
            .filter { s -> s.isNotBlank() && s.all { c -> c.isLetterOrDigit() } }
            .bind()
        val domain = Arb.string(minSize = 2, maxSize = 8)
            .filter { s -> s.isNotBlank() && s.all { c -> c.isLetterOrDigit() } }
            .bind()
        "$local@$domain.com"
    }

    // Generates protected (non-public) endpoint paths
    val arbProtectedPath: Arb<String> = Arb.string(minSize = 1, maxSize = 20)
        .filter { s ->
            s.isNotBlank() &&
            s.all { c -> c.isLetterOrDigit() || c == '-' || c == '_' } &&
            !s.startsWith("auth") &&
            !s.startsWith("api")
        }
        .let { arb -> arbitrary { "/protected/${arb.bind()}" } }

    beforeEach {
        SecurityContextHolder.clearContext()
    }

    // Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
    // Validates: Requirements 2.4, 2.5, 9.1
    "requisicao sem token para endpoint protegido deve ser rejeitada com 401" {
        checkAll(100, arbProtectedPath) { path ->
            SecurityContextHolder.clearContext()

            val request = MockHttpServletRequest("GET", path)
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, filterChain)

            response.status shouldBe 401
            verify(exactly = 0) { filterChain.doFilter(any(), any()) }
        }
    }

    // Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
    // Validates: Requirements 2.4, 2.5, 9.1
    "requisicao com token de assinatura invalida para endpoint protegido deve ser rejeitada com 401" {
        val wrongKey = Keys.hmacShaKeyFor("wrong-secret-key-totally-different-x!".toByteArray())

        checkAll(100, arbEmail, arbUserId, arbProtectedPath) { email, userId, path ->
            SecurityContextHolder.clearContext()

            val invalidToken = Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact()

            val request = MockHttpServletRequest("GET", path)
            request.addHeader("Authorization", "Bearer $invalidToken")
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, filterChain)

            response.status shouldBe 401
            verify(exactly = 0) { filterChain.doFilter(any(), any()) }
        }
    }

    // Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
    // Validates: Requirements 2.4, 2.5, 9.1
    "requisicao com token expirado para endpoint protegido deve ser rejeitada com 401" {
        checkAll(100, arbEmail, arbUserId, arbProtectedPath) { email, userId, path ->
            SecurityContextHolder.clearContext()

            val expiredToken = Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(Date(System.currentTimeMillis() - 2 * expirationTimeMs))
                .setExpiration(Date(System.currentTimeMillis() - expirationTimeMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact()

            val request = MockHttpServletRequest("GET", path)
            request.addHeader("Authorization", "Bearer $expiredToken")
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, filterChain)

            response.status shouldBe 401
            verify(exactly = 0) { filterChain.doFilter(any(), any()) }
        }
    }

    // Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
    // Validates: Requirements 2.4, 2.5, 9.1
    "requisicao com token valido para endpoint protegido deve ser autorizada e userId injetado no SecurityContext" {
        checkAll(100, arbEmail, arbUserId, arbProtectedPath) { email, userId, path ->
            SecurityContextHolder.clearContext()

            val validToken = jwtUtil.generateToken(email, userId)

            val request = MockHttpServletRequest("GET", path)
            request.addHeader("Authorization", "Bearer $validToken")
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, filterChain)

            response.status shouldBe 200
            verify(exactly = 1) { filterChain.doFilter(request, response) }

            val authentication = SecurityContextHolder.getContext().authentication
            authentication shouldBe authentication // non-null
            (authentication?.principal as? String) shouldBe userId
        }
    }

    // Feature: personal-expense-control, Property 6: Apenas JWT válido concede acesso a endpoints protegidos
    // Validates: Requirements 2.4, 9.1
    "requisicoes para endpoints publicos devem passar sem token" {
        val publicPaths = listOf("/auth/register", "/auth/login", "/api/public")

        checkAll(100, arbEmail, arbUserId) { email, userId ->
            publicPaths.forEach { path ->
                SecurityContextHolder.clearContext()

                val request = MockHttpServletRequest("POST", path)
                val response = MockHttpServletResponse()
                val filterChain = mockk<FilterChain>(relaxed = true)

                filter.doFilter(request, response, filterChain)

                response.status shouldBe 200
                verify(exactly = 1) { filterChain.doFilter(request, response) }
            }
        }
    }
})
