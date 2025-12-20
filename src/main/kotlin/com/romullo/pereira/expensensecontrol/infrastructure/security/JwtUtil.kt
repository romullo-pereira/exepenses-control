package com.romullo.pereira.expensensecontrol.infrastructure.security

import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.infrastructure.config.logger
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil(
    @Value("\${environment.config.token-expiration}")
    private val expirationTime: Long,
    @Value("\${environment.config.secret-key}")
    private val secretKeyString: String
) {
    private val secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    private val logger = logger()

    fun generateToken(email: String, userId: ObjectId): String =
        Jwts
            .builder()
            .setSubject(email)
            .claim("userId", userId.toString())
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
            logger.error("Erro ao validar token: ${e.message}")
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

    fun getUserIdFromToken(token: String): String {
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        return claims["userId"].toString()
    }
}
