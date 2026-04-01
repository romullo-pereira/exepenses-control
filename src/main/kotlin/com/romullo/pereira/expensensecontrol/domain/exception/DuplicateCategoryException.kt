package com.romullo.pereira.expensensecontrol.domain.exception

class DuplicateCategoryException(
    override val message: String,
) : RuntimeException(message)
