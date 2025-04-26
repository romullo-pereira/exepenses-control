package com.romullo.pereira.expensensecontrol.domain.commons

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun `isValidEmail deve retornar true para emails validos`() {
        assertTrue("teste@example.com".isValidEmail())
        assertTrue("user.name+tag+sorting@example.com".isValidEmail())
        assertTrue("user_name@example.co.uk".isValidEmail())
    }

    @Test
    fun `isValidEmail deve retornar false para emails invalidos`() {
        assertFalse("plainaddress".isValidEmail())
        assertFalse("@missingusername.com".isValidEmail())
        assertFalse("username@.com".isValidEmail())
        assertFalse("username@com".isValidEmail())
    }

    @Test
    fun `isValidPassword deve retornar true para senhas validas`() {
        assertTrue("Password1.".isValidPassword())
        assertTrue("Valid123.".isValidPassword())
        assertTrue("Another1.".isValidPassword())
    }

    @Test
    fun `isValidPassword deve retornar false para senhas invalidas`() {
        assertFalse("short".isValidPassword())
        assertFalse("nouppercase1".isValidPassword())
        assertFalse("NoNumber".isValidPassword())
    }
}