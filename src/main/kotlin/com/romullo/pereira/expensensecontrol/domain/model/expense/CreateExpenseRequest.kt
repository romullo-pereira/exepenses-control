package com.romullo.pereira.expensensecontrol.domain.model.expense

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.Instant

data class CreateExpenseRequest(
    @field:Positive
    val amount: Double,
    @field:NotBlank
    val category: String,
    val date: Instant,
    @field:NotBlank
    val description: String,
)
