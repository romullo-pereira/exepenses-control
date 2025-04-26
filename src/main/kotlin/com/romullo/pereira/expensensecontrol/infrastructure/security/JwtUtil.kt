package com.romullo.pereira.expensensecontrol.infrastructure.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil(
    @Value("\${environment.config.token-expiration}")
    private val expirationTime: Long,
) {
    companion object {
        private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    }

    fun generateToken(email: String): String =
        Jwts
            .builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact()

    fun validateToken(token: String): Boolean =
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getEmailFromToken(token: String): String {
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        return claims.subject
    }
}
