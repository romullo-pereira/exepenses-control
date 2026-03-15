package com.romullo.pereira.expensensecontrol.domain.model.login

data class TokenResponse(
    val token: String,
    val expiresIn: Long,
)
