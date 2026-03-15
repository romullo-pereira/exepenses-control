package com.romullo.pereira.expensensecontrol.domain.model.login

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LoginRequestTest {

    @Test
    fun `LoginRequest should hold email and password`() {
        val request = LoginRequest(email = "teste@example.com", password = "Password1!")
        assertNotNull(request.email)
        assertNotNull(request.password)
    }

    @Test
    fun `LoginRequest email should be set correctly`() {
        val email = "user@example.com"
        val request = LoginRequest(email = email, password = "Password1!")
        assert(request.email == email)
    }
}
