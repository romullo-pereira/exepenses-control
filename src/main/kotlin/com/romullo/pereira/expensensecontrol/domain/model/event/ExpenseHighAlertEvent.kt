package com.romullo.pereira.expensensecontrol.domain.model.event

data class ExpenseHighAlertEvent(
    val userId: String,
    val amount: Double,
)
