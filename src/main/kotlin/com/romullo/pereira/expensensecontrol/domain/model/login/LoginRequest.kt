package com.romullo.pereira.expensensecontrol.domain.model.login

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String,
)
