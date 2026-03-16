package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security.JwtUtil
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidCredentialsException
import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.login.TokenResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.AuthenticateUserUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticateUserUseCaseImpl(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    @Value("\${environment.config.token-expiration}")
    private val expirationTime: Long,
) : AuthenticateUserUseCase {

    private val logger = logger()

    override fun authenticate(loginRequest: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw InvalidCredentialsException("Usuário ou senha incorretos.")
        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) {
            throw InvalidCredentialsException("Usuário ou senha incorretos.")
        }
        return TokenResponse(
            token = jwtUtil.generateToken(user.email, user.id),
            expiresIn = expirationTime,
        )
    }
}
