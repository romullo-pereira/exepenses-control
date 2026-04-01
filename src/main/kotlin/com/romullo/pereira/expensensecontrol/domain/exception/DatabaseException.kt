package com.romullo.pereira.expensensecontrol.domain.exception

class DatabaseException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
