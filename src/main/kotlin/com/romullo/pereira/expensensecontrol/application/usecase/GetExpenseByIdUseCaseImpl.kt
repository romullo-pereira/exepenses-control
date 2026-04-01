package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.domain.exception.ForbiddenException
import com.romullo.pereira.expensensecontrol.domain.exception.NotFoundException
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.GetExpenseByIdUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import org.springframework.stereotype.Service

@Service
class GetExpenseByIdUseCaseImpl(
    private val expenseRepository: ExpenseRepositoryPort,
) : GetExpenseByIdUseCase {

    override fun getById(id: String, userId: String): ExpenseResponse {
        val expense = expenseRepository.findById(id)
            ?: throw NotFoundException("Despesa não encontrada.")

        if (expense.userId != userId) {
            throw ForbiddenException("Acesso negado à despesa solicitada.")
        }

        return expense.toResponse()
    }
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
