package com.romullo.pereira.expensensecontrol.adapters.inbound.rest

import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.login.TokenResponse
import com.romullo.pereira.expensensecontrol.domain.model.user.RegisterRequest
import com.romullo.pereira.expensensecontrol.domain.port.inbound.AuthenticateUserUseCase
import com.romullo.pereira.expensensecontrol.domain.port.inbound.RegisterUserUseCase
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val authenticateUserUseCase: AuthenticateUserUseCase,
) {
    private val logger = logger()

    @PostMapping("/register")
    fun createUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Any> {
        logger.info("Received request create user to email ${registerRequest.email}")
        registerUserUseCase.register(registerRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        logger.info("Received request login to email ${loginRequest.email}")
        return ResponseEntity.status(HttpStatus.OK).body(authenticateUserUseCase.authenticate(loginRequest))
    }
}
