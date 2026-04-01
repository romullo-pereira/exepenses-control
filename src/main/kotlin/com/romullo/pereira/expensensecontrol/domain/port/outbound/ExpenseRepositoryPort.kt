package com.romullo.pereira.expensensecontrol.domain.port.outbound

import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense

interface ExpenseRepositoryPort {
    fun save(expense: Expense): Expense
    fun findByUserId(userId: String): List<Expense>
    fun findById(id: String): Expense?
    fun findByIdAndUserId(id: String, userId: String): Expense?
    fun existsByExternalIdAndUserId(externalId: String, userId: String): Boolean
}
