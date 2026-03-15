package com.romullo.pereira.expensensecontrol.domain.exception

class InvalidCredentialsException(
    override val message: String,
) : RuntimeException(message)
