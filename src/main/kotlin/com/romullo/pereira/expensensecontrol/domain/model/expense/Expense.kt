package com.romullo.pereira.expensensecontrol.domain.model.expense

import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "expenses")
data class Expense(
    @Id
    val id: String = ObjectId.get().toString(),
    val userId: String,
    val amount: Double,
    val category: String,
    val date: Instant,
    val description: String,
    val source: ExpenseSource,
    val externalId: String? = null,
)
