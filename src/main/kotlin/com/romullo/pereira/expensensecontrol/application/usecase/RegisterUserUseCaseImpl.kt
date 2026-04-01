package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import com.romullo.pereira.expensensecontrol.domain.exception.DuplicateEmailException
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidInputException
import com.romullo.pereira.expensensecontrol.domain.model.user.RegisterRequest
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.port.inbound.RegisterUserUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCaseImpl(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
) : RegisterUserUseCase {

    private val logger = logger()

    private val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    override fun register(request: RegisterRequest): User {
        if (!emailRegex.matches(request.email)) {
            throw InvalidInputException("E-mail em formato inválido.")
        }
        if (request.password.length < 8) {
            throw InvalidInputException("A senha deve ter no mínimo 8 caracteres.")
        }
        if (userRepository.existsByEmail(request.email)) {
            logger.debug("Email já cadastrado: ${request.email}")
            throw DuplicateEmailException("Email já cadastrado.")
        }
        return userRepository.save(
            User(
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password),
            )
        )
    }
}
