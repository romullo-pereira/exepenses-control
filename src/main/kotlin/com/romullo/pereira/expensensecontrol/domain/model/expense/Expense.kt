package com.romullo.pereira.expensensecontrol.domain.model.expense

import com.romullo.pereira.expensensecontrol.domain.model.enum.CategoriesEnum
import com.romullo.pereira.expensensecontrol.domain.model.enum.SourceEnum
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.ZonedDateTime

@Document(collection = "expenses")
data class Expense(
    @Id
    val id: ObjectId = ObjectId.get(),
    val userId: String,
    val amount: BigDecimal,
    val category: CategoriesEnum,
    val date: ZonedDateTime,
    val description: String? = null,
    val source: SourceEnum
)