package com.romullo.pereira.expensensecontrol.domain.model.expense

import java.time.Instant

data class ExpenseResponse(
    val id: String,
    val userId: String,
    val amount: Double,
    val category: String,
    val date: Instant,
    val description: String,
    val source: String,
)
