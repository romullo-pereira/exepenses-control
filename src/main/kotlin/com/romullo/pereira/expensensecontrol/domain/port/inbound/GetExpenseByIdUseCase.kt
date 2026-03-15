package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse

interface GetExpenseByIdUseCase {
    fun getById(id: String, userId: String): ExpenseResponse
}
