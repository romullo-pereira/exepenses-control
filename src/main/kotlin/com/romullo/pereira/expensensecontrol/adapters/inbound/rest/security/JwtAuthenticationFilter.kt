package com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import com.romullo.pereira.expensensecontrol.domain.exception.UnauthorizedException
import com.romullo.pereira.expensensecontrol.domain.model.error.BusinessError
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    private val logger = logger()

    private val publicPaths = listOf("/auth/register", "/auth/login", "/api/public")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestPath = request.requestURI
        val isPublicPath = publicPaths.any { requestPath.startsWith(it) }

        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            if (!isPublicPath) {
                logger.warn("Token ausente na requisição para: $requestPath")
                writeUnauthorizedResponse(response, "Token de autenticação ausente")
                return
            }
            filterChain.doFilter(request, response)
            return
        }

        val token = authorizationHeader.substring(7)

        if (!jwtUtil.validateToken(token)) {
            logger.warn("Token inválido ou expirado na requisição para: $requestPath")
            writeUnauthorizedResponse(response, "Token inválido ou expirado")
            return
        }

        try {
            val userId = jwtUtil.getUserIdFromToken(token)
            if (SecurityContextHolder.getContext().authentication == null) {
                val authorities = emptyList<GrantedAuthority>()
                val authentication = JwtAuthenticationToken(userId, null, authorities)
                authentication.isAuthenticated = true
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
                logger.info("SecurityContext configurado com userId: $userId")
            }
        } catch (e: Exception) {
            logger.error("Erro ao processar token: ${e.message}")
            writeUnauthorizedResponse(response, "Token inválido ou expirado")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun writeUnauthorizedResponse(response: HttpServletResponse, message: String) {
        val error = BusinessError(HttpStatus.UNAUTHORIZED.value(), message)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        objectMapper.writeValue(response.writer, error)
    }
}
