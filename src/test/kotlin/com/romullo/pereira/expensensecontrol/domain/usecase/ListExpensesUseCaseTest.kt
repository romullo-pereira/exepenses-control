package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.application.usecase.ListExpensesUseCaseImpl
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
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

// Feature: personal-expense-control, Property 11: Isolamento de despesas por usuário na listagem
// Feature: personal-expense-control, Property 12: Despesas listadas em ordem decrescente de data
class ListExpensesUseCaseTest : StringSpec({

    val expenseRepository = mockk<ExpenseRepositoryPort>()

    val useCase = ListExpensesUseCaseImpl(
        expenseRepository = expenseRepository,
    )

    // Generators
    val arbUserId: Arb<String> = Arb.string(minSize = 1, maxSize = 24)
        .filter { it.isNotBlank() && it.all { c -> c.isLetterOrDigit() } }

    val arbInstant: Arb<Instant> = arbitrary {
        val epochSecond = Arb.long(0L, 9_999_999_999L).bind()
        Instant.ofEpochSecond(epochSecond)
    }

    val arbExpenseForUser: Arb<Pair<String, Expense>> = arbitrary {
        val userId = arbUserId.bind()
        val expense = Expense(
            userId = userId,
            amount = Arb.double(min = 0.01, max = 1_000_000.0).filter { it > 0.0 && it.isFinite() }.bind(),
            category = Arb.string(minSize = 1, maxSize = 50).filter { it.isNotBlank() }.bind(),
            date = arbInstant.bind(),
            description = Arb.string(minSize = 0, maxSize = 200).bind(),
            source = Arb.enum<ExpenseSource>().bind(),
        )
        Pair(userId, expense)
    }

    // Feature: personal-expense-control, Property 11: Isolamento de despesas por usuário na listagem
    // Validates: Requirements 4.1, 9.2
    "listagem de despesas deve retornar exclusivamente as despesas do usuario autenticado, sem despesas de outros usuarios" {
        checkAll(100, arbUserId, Arb.list(arbExpenseForUser, range = 0..20)) { requestedUserId, expensePairs ->

            // Build a mixed list of expenses belonging to various users
            val allExpenses = expensePairs.map { it.second }

            // The repository returns only expenses matching the requested userId (simulating DB filter)
            val userExpenses = allExpenses.filter { it.userId == requestedUserId }
            every { expenseRepository.findByUserId(requestedUserId) } returns userExpenses

            val result = useCase.listByUser(requestedUserId)

            // All returned expenses must belong to the requested user
            result.all { it.userId == requestedUserId } shouldBe true

            // No expense from another user must appear in the result
            result.none { it.userId != requestedUserId } shouldBe true

            // The count must match the number of expenses for that user
            result.size shouldBe userExpenses.size
        }
    }

    // Feature: personal-expense-control, Property 12: Despesas listadas em ordem decrescente de data
    // Validates: Requirements 4.2
    "despesas listadas devem estar em ordem decrescente de data" {
        checkAll(100, arbUserId, Arb.list(arbExpenseForUser, range = 2..20)) { userId, expensePairs ->

            val userExpenses = expensePairs.map { (_, expense) -> expense.copy(userId = userId) }
            every { expenseRepository.findByUserId(userId) } returns userExpenses

            val result = useCase.listByUser(userId)

            // For every consecutive pair, the earlier element must have date >= the later one
            result.zipWithNext().all { (a, b) -> !a.date.isBefore(b.date) } shouldBe true
        }
    }
})
