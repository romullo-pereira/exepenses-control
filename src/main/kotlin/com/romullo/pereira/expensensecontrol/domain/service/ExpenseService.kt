package com.romullo.pereira.expensensecontrol.domain.service

import com.romullo.pereira.expensensecontrol.domain.model.enum.SourceEnum
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.infrastructure.persistence.ExpensesRepository
import com.romullo.pereira.expensensecontrol.infrastructure.security.JwtUtil
import org.springframework.stereotype.Service

@Service
class ExpenseService(
    private val expensesRepository: ExpensesRepository,
    private val jwtUtil: JwtUtil
) {

    fun createExpense(expense: ExpenseRequest, token: String): ExpenseResponse =
        buildResponse(
            expensesRepository.save(
                buildExpense(expense, token)
            )
        )



    private fun buildExpense(expenseRequest: ExpenseRequest, token: String) =
        Expense(
            userId = jwtUtil.getUserIdFromToken(token),
            description = expenseRequest.description,
            amount = expenseRequest.amount,
            date = expenseRequest.date,
            category = expenseRequest.category,
            source = SourceEnum.MANUAL,
        )
    private fun buildResponse(expense: Expense) =
        ExpenseResponse(
            id = expense.id,
            userId = expense.userId,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            category = expense.category,
            source = expense.source,
        )

//    fun getExpensesByUserId(userId: String): List<Expense> {
//        val expenses =
//    }
}