package com.romullo.pereira.expensensecontrol.domain.model.user

data class UserRequest(
        val email: String,
        val password: String,
        val categories: Set<String>
)