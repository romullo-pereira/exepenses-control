package com.romullo.pereira.expensensecontrol.domain.exception

class NotFoundException(
    override val message: String,
) : RuntimeException(message)
