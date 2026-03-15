package com.romullo.pereira.expensensecontrol.domain.exception

class ForbiddenException(
    override val message: String,
) : RuntimeException(message)
