package com.romullo.pereira.expensensecontrol.infrastructure.persistence

import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ExpensesRepository : MongoRepository<Expense, ObjectId> {

    fun findByUserId(userId: ObjectId): List<Expense>

}
