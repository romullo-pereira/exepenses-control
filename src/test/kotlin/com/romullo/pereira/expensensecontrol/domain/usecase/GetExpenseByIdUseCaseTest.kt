package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.application.usecase.GetExpenseByIdUseCaseImpl
import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.time.Instant

// Feature: personal-expense-control, Property 13: Round trip de consulta de despesa por id
class GetExpenseByIdUseCaseTest : StringSpec({

    // In-memory implementation of ExpenseRepositoryPort for isolation
    class InMemoryExpenseRepository(initialExpenses: List<Expense> = emptyList()) : ExpenseRepositoryPort {
        private val store = initialExpenses.associateBy { it.id }.toMutableMap()

        override fun save(expense: Expense): Expense {
            store[expense.id] = expense
            return expense
        }

        override fun findByUserId(userId: String): List<Expense> =
            store.values.filter { it.userId == userId }

        override fun findById(id: String): Expense? = store[id]

        override fun findByIdAndUserId(id: String, userId: String): Expense? =
            store[id]?.takeIf { it.userId == userId }

        override fun existsByExternalIdAndUserId(externalId: String, userId: String): Boolean =
            store.values.any { it.externalId == externalId && it.userId == userId }
    }

    val arbExpense: Arb<Expense> = arbitrary {
        Expense(
            userId = Arb.string(minSize = 1, maxSize = 50)
                .filter { it.isNotBlank() }.bind(),
            amount = Arb.double(min = 0.01, max = 1_000_000.0)
                .filter { it > 0.0 }.bind(),
            category = Arb.string(minSize = 1, maxSize = 50)
                .filter { it.isNotBlank() }.bind(),
            date = Instant.now(),
            description = Arb.string(minSize = 0, maxSize = 200).bind(),
            source = Arb.enum<ExpenseSource>().bind(),
        )
    }

    // Feature: personal-expense-control, Property 13: Round trip de consulta de despesa por id
    // Validates: Requirements 5.1
    "para qualquer despesa persistida pertencente ao usuario autenticado, consultar pelo id deve retornar todos os campos iguais aos da despesa original" {
        checkAll(100, arbExpense) { expense ->
            val repo = InMemoryExpenseRepository(listOf(expense))
            val useCase = GetExpenseByIdUseCaseImpl(repo)

            val result = useCase.getById(expense.id, expense.userId)

            result.amount shouldBe expense.amount
            result.category shouldBe expense.category
            result.date shouldBe expense.date
            result.description shouldBe expense.description
            result.source shouldBe expense.source.name
        }
    }
})
