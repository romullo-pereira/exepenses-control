package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse

interface ListExpensesUseCase {
    fun listByUser(userId: String): List<ExpenseResponse>
}
