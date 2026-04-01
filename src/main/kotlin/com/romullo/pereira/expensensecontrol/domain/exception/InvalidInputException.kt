package com.romullo.pereira.expensensecontrol.domain.exception

class InvalidInputException(
    override val message: String,
) : RuntimeException(message)
