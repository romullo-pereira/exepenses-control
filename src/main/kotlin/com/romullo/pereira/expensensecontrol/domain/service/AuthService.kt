package com.romullo.pereira.expensensecontrol.domain.service

import com.romullo.pereira.expensensecontrol.domain.exception.DuplicatedEmailException
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidUserException
import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.login.LoginResponse
import com.romullo.pereira.expensensecontrol.infrastructure.config.logger
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.model.user.UserRequest
import com.romullo.pereira.expensensecontrol.infrastructure.persistence.UserRepository
import com.romullo.pereira.expensensecontrol.infrastructure.security.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtUtil: JwtUtil,
        @Value("\${environment.config.token-expiration}")
        private val expirationTime: Long
) {
    private val logger = logger()
    fun createUser(userRequest: UserRequest): User {

        if (userRepository.findByEmail(userRequest.email).isNotEmpty()) {
            logger.error("Email ja cadastrado")
            throw DuplicatedEmailException()
        }
        return userRepository.save(buildUser(userRequest))
    }

    fun login(loginRequest: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(loginRequest.email).firstOrNull()
                ?: throw InvalidUserException()

        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            throw InvalidUserException()
        }
        return buildLoginResponse(user)
    }

    private fun buildUser(userRequest: UserRequest) =
            User (
                    email = userRequest.email,
                    password = encodePassword(userRequest.password),
                    categories = userRequest.categories
            )

    private fun buildLoginResponse(user: User) =
            LoginResponse(
                    token = jwtUtil.generateToken(user.email),
                    expirationTime = expirationTime,
                    email = user.email,
                    categories = user.categories

            )
    private fun encodePassword(password: String) = passwordEncoder.encode(password)
}