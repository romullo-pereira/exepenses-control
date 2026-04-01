package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.ListExpensesUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import org.springframework.stereotype.Service

@Service
class ListExpensesUseCaseImpl(
    private val expenseRepository: ExpenseRepositoryPort,
) : ListExpensesUseCase {

    override fun listByUser(userId: String): List<ExpenseResponse> =
        expenseRepository.findByUserId(userId)
            .sortedByDescending { it.date }
            .map { it.toResponse() }
}

private fun Expense.toResponse() = ExpenseResponse(
    id = id,
    userId = userId,
    amount = amount,
    category = category,
    date = date,
    description = description,
    source = source.name,
)
