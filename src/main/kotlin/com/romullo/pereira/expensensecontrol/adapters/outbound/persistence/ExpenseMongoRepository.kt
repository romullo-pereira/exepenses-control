package com.romullo.pereira.expensensecontrol.adapters.outbound.persistence

import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import org.springframework.data.mongodb.repository.MongoRepository

interface ExpenseMongoRepository : MongoRepository<Expense, String> {
    fun findByUserId(userId: String): List<Expense>
}
