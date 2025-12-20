package com.romullo.pereira.expensensecontrol.domain.model.expense

import com.romullo.pereira.expensensecontrol.domain.model.enum.CategoriesEnum
import com.romullo.pereira.expensensecontrol.domain.model.enum.SourceEnum
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.ZonedDateTime

data class ExpenseResponse(
    val id: ObjectId,
    val userId: String,
    val amount: BigDecimal,
    val category: CategoriesEnum,
    val date: ZonedDateTime,
    val description: String? = null,
    val source: SourceEnum
)