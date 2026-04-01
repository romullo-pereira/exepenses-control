package com.romullo.pereira.expensensecontrol.domain.exception

class UnauthorizedException(
    override val message: String,
) : RuntimeException(message)
