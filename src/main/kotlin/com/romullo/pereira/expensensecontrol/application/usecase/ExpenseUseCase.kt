package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.expense.CreateExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.CreateExpenseUseCase
import com.romullo.pereira.expensensecontrol.domain.port.inbound.GetExpenseByIdUseCase
import com.romullo.pereira.expensensecontrol.domain.port.inbound.ListExpensesUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import org.springframework.stereotype.Service

@Service
class ExpenseUseCase(
    private val expenseRepository: ExpenseRepositoryPort,
) : CreateExpenseUseCase, ListExpensesUseCase, GetExpenseByIdUseCase {

    override fun create(request: CreateExpenseRequest, userId: String): ExpenseResponse {
        val expense = expenseRepository.save(
            Expense(
                userId = userId,
                description = request.description,
                amount = request.amount,
                date = request.date,
                category = request.category,
                source = ExpenseSource.MANUAL,
            )
        )
        return buildResponse(expense)
    }

    override fun listByUser(userId: String): List<ExpenseResponse> =
        expenseRepository.findByUserId(userId).map { buildResponse(it) }

    override fun getById(id: String, userId: String): ExpenseResponse {
        val expense = expenseRepository.findByIdAndUserId(id, userId)
            ?: throw NoSuchElementException("Expense not found")
        return buildResponse(expense)
    }

    private fun buildResponse(expense: Expense) = ExpenseResponse(
        id = expense.id,
        userId = expense.userId,
        description = expense.description,
        amount = expense.amount,
        date = expense.date,
        category = expense.category,
        source = expense.source.name,
    )
}
