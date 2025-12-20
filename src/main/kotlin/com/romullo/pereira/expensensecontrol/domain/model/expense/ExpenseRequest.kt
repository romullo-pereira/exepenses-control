package com.romullo.pereira.expensensecontrol.domain.model.expense

import com.romullo.pereira.expensensecontrol.domain.model.enum.CategoriesEnum
import com.romullo.pereira.expensensecontrol.domain.model.enum.SourceEnum
import java.math.BigDecimal
import java.time.ZonedDateTime


data class ExpenseRequest(
    val amount: BigDecimal,
    val category: CategoriesEnum,
    val date: ZonedDateTime = ZonedDateTime.now(),
    val description: String? = null
)