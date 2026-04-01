package com.romullo.pereira.expensensecontrol.domain.exception

class DuplicateEmailException(
    override val message: String,
) : RuntimeException(message)
