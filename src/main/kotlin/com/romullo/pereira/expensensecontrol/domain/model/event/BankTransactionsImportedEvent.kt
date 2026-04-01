package com.romullo.pereira.expensensecontrol.domain.model.event

data class BankTransactionsImportedEvent(
    val userId: String,
    val count: Int,
)
