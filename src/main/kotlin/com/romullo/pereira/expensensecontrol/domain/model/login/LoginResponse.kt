package com.romullo.pereira.expensensecontrol.domain.model.login

data class LoginResponse(
    val token: String,
    val expirationTime: Long,
    val email: String,
    val categories: Set<String>,
)