package com.romullo.pereira.expensensecontrol.domain.model.event

data class ExpenseCreatedEvent(
    val expenseId: String,
    val userId: String,
    val amount: Double,
    val category: String,
)
