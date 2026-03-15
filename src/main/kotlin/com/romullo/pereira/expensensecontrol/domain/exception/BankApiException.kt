package com.romullo.pereira.expensensecontrol.domain.exception

class BankApiException(
    override val message: String,
) : RuntimeException(message)
