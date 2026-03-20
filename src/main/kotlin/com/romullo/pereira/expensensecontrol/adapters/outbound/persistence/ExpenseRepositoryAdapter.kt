package com.romullo.pereira.expensensecontrol.adapters.outbound.persistence

import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import org.springframework.stereotype.Component

@Component
class ExpenseRepositoryAdapter(
    private val expenseMongoRepository: ExpenseMongoRepository,
) : ExpenseRepositoryPort {

    override fun save(expense: Expense): Expense = expenseMongoRepository.save(expense)

    override fun findByUserId(userId: String): List<Expense> =
        expenseMongoRepository.findByUserId(userId)

    override fun findByIdAndUserId(id: String, userId: String): Expense? =
        expenseMongoRepository.findById(id).orElse(null)
            ?.takeIf { it.userId == userId }

    override fun findById(id: String): Expense? =
        expenseMongoRepository.findById(id).orElse(null)

    override fun existsByExternalIdAndUserId(externalId: String, userId: String): Boolean = false
}
