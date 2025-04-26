package com.romullo.pereira.expensensecontrol.domain.model.user

import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_EMAIL
import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages.INVALID_PASSWORD
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserRequestTest {

    @Test
    fun `validate should throw exception to invalid email`() {
        val userRequest = UserRequest(email = "email_invalido", password = "Password1!")
        val exception = assertThrows<IllegalArgumentException> { userRequest.validate() }
        assert(exception.message == INVALID_EMAIL)
    }

    @Test
    fun `validate should throw an excpetion to invalid password`() {
        val userRequest = UserRequest(email = "teste@example.com", password = "senha")
        val exception = assertThrows<IllegalArgumentException> { userRequest.validate() }
        assert(exception.message == INVALID_PASSWORD)
    }

    @Test
    fun `validate should not throw an exception to valids email and password`() {
        val userRequest = UserRequest(email = "teste@example.com", password = "Password1!")
        assertDoesNotThrow { userRequest.validate() }
    }
}