package com.romullo.pereira.expensensecontrol.domain.model.login

import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_EMAIL
import com.romullo.pereira.expensensecontrol.domain.commons.isValidEmail

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun validate(): LoginRequest {
        if (email.isNotBlank() && !email.isValidEmail()) {
            throw IllegalArgumentException(INVALID_EMAIL)
        }
        return this
    }
}
