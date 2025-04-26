package com.romullo.pereira.expensensecontrol.domain.model.user

import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_EMAIL
import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_PASSWORD
import com.romullo.pereira.expensensecontrol.domain.commons.isValidEmail
import com.romullo.pereira.expensensecontrol.domain.commons.isValidPassword

data class UserRequest(
    val email: String,
    val password: String,
) {
    fun validate(): UserRequest {
        if (email.isNotBlank() && !email.isValidEmail()) {
            throw IllegalArgumentException(INVALID_EMAIL)
        }
        if (password.isNotBlank() && !password.isValidPassword()) {
            throw IllegalArgumentException(INVALID_PASSWORD)
        }

        return this
    }
}
