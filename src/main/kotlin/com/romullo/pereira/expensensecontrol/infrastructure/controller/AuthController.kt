package com.romullo.pereira.expensensecontrol.infrastructure.controller

import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.login.LoginResponse
import com.romullo.pereira.expensensecontrol.domain.model.user.UserRequest
import com.romullo.pereira.expensensecontrol.domain.service.AuthService
import com.romullo.pereira.expensensecontrol.infrastructure.config.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    val authService: AuthService,
) {
    private val logger = logger()

    @PostMapping("/register")
    fun createUser(
        @RequestBody userRequest: UserRequest,
    ): ResponseEntity<Any> {
        logger.info("Received request create user to email ${userRequest.email}")
        authService.createUser(userRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        logger.info("Received request login to email ${loginRequest.email}")
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest))
    }
}
