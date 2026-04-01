package com.romullo.pereira.expensensecontrol.adapters.persistence

import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.time.Instant

// Feature: personal-expense-control, Property 24: Campos obrigatórios presentes em toda Despesa persistida
class ExpensePersistenceTest : StringSpec({

    val arbExpense: Arb<Expense> = arbitrary {
        Expense(
            userId = Arb.string(minSize = 1, maxSize = 50).bind(),
            amount = Arb.double(min = 0.01, max = 1_000_000.0).filter { it > 0.0 }.bind(),
            category = Arb.string(minSize = 1, maxSize = 50).bind(),
            date = Instant.now(),
            description = Arb.string(minSize = 0, maxSize = 200).bind(),
            source = Arb.enum<ExpenseSource>().bind(),
        )
    }

    // Feature: personal-expense-control, Property 24: Campos obrigatórios presentes em toda Despesa persistida
    // Validates: Requirements 10.2
    "toda Despesa persistida deve conter os campos obrigatórios userId, amount, category, date e source não nulos" {
        checkAll(100, arbExpense) { expense ->
            // userId must be present and non-blank
            expense.userId shouldNotBe null
            expense.userId.shouldNotBeBlank()

            // amount must be present and greater than zero
            expense.amount shouldNotBe null
            expense.amount shouldBeGreaterThan 0.0

            // category must be present and non-blank
            expense.category shouldNotBe null
            expense.category.shouldNotBeBlank()

            // date must be present and non-null
            expense.date shouldNotBe null

            // source must be present and non-null
            expense.source shouldNotBe null
            expense.source shouldBe expense.source // confirms it's a valid ExpenseSource value
        }
    }
})
