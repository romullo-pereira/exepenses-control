package com.romullo.pereira.expensensecontrol.domain.model.login

import org.junit.jupiter.api.assertThrows
import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_EMAIL
import org.junit.jupiter.api.Assertions.assertDoesNotThrow

import org.junit.jupiter.api.Test

class LoginRequestTest {

    @Test
    fun `validate should throw an exception to invalid email`() {
        val loginRequest = LoginRequest(email = "email_invalido", password = "Password1!")
        val exception = assertThrows<IllegalArgumentException> { loginRequest.validate() }
        assert(exception.message == INVALID_EMAIL)
    }

    @Test
    fun `validate should not throw an exception to valid email`() {
        val loginRequest = LoginRequest(email = "teste@example.com", password = "Password1!")
        assertDoesNotThrow { loginRequest.validate() }
    }
}