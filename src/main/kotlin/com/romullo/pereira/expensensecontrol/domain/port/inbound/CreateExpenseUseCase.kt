package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.expense.CreateExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse

interface CreateExpenseUseCase {
    fun create(request: CreateExpenseRequest, userId: String): ExpenseResponse
}
