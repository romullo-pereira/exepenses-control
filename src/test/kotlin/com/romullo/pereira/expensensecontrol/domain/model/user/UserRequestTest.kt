package com.romullo.pereira.expensensecontrol.domain.model.user

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class UserRequestTest {

    @Test
    fun `RegisterRequest should hold email and password`() {
        val request = RegisterRequest(email = "teste@example.com", password = "Password1!")
        assertNotNull(request.email)
        assertNotNull(request.password)
    }

    @Test
    fun `RegisterRequest email should be set correctly`() {
        val email = "user@example.com"
        val request = RegisterRequest(email = email, password = "Password1!")
        assert(request.email == email)
    }

    @Test
    fun `RegisterRequest password should be set correctly`() {
        val password = "SecurePass1!"
        val request = RegisterRequest(email = "user@example.com", password = password)
        assert(request.password == password)
    }
}
