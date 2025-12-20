package com.romullo.pereira.expensensecontrol.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)
            logger.info("Token recebido: $token")
            if (jwtUtil.validateToken(token) && SecurityContextHolder.getContext().authentication == null) {
                val email = jwtUtil.getEmailFromToken(token)
                val authorities = emptyList<GrantedAuthority>()
                val authentication = JwtAuthenticationToken(email, null, authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
                logger.info("SecurityContextHolder configurado com sucesso para o usuário: $email")
            } else {
                logger.warn("Token inválido ou contexto já autenticado")
            }
        }
        filterChain.doFilter(request, response)
    }
}
