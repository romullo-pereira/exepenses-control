package com.romullo.pereira.expensensecontrol.domain.usecase

import com.romullo.pereira.expensensecontrol.application.usecase.CreateExpenseUseCaseImpl
import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import com.romullo.pereira.expensensecontrol.domain.model.expense.CreateExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.port.outbound.EventPublisherPort
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant

// Feature: personal-expense-control, Property 7: Criação de despesa persiste com source=MANUAL
class CreateExpenseUseCaseTest : StringSpec({

    val expenseRepository = mockk<ExpenseRepositoryPort>()
    val userRepository = mockk<UserRepositoryPort>()
    val eventPublisher = mockk<EventPublisherPort>(relaxed = true)

    val useCase = CreateExpenseUseCaseImpl(
        expenseRepository = expenseRepository,
        userRepository = userRepository,
        eventPublisher = eventPublisher,
    )

    // Generators
    val arbAmount: Arb<Double> = Arb.double(0.01, 10_000.0).filter { it > 0.0 && it.isFinite() }

    val arbNonBlankString: Arb<String> = Arb.string(minSize = 1, maxSize = 50)
        .filter { it.isNotBlank() }

    val arbInstant: Arb<Instant> = arbitrary {
        val epochSecond = Arb.long(0L, 9_999_999_999L).bind()
        Instant.ofEpochSecond(epochSecond)
    }

    val arbUserId: Arb<String> = Arb.string(minSize = 1, maxSize = 24)
        .filter { it.isNotBlank() && it.all { c -> c.isLetterOrDigit() } }

    // Feature: personal-expense-control, Property 7: Criação de despesa persiste com source=MANUAL
    // Validates: Requirements 3.1
    "para qualquer requisicao valida, a despesa persistida deve ter source=MANUAL e conter exatamente os dados da requisicao" {
        checkAll(100, arbAmount, arbNonBlankString, arbInstant, arbNonBlankString, arbUserId) {
            amount, category, date, description, userId ->

            val savedExpenseSlot = slot<Expense>()

            every { expenseRepository.save(capture(savedExpenseSlot)) } answers {
                savedExpenseSlot.captured
            }

            every { userRepository.findById(userId) } returns User(
                id = userId,
                email = "$userId@test.com",
                passwordHash = "hash",
                expenseLimit = null,
            )

            val request = CreateExpenseRequest(
                amount = amount,
                category = category,
                date = date,
                description = description,
            )

            val response = useCase.create(request, userId)

            // source must be MANUAL
            response.source shouldBe ExpenseSource.MANUAL.name

            // persisted expense must have source = MANUAL
            savedExpenseSlot.captured.source shouldBe ExpenseSource.MANUAL

            // fields must match the request exactly
            savedExpenseSlot.captured.amount shouldBe amount
            savedExpenseSlot.captured.category shouldBe category
            savedExpenseSlot.captured.date shouldBe date
            savedExpenseSlot.captured.description shouldBe description
            savedExpenseSlot.captured.userId shouldBe userId

            // response fields must also match
            response.amount shouldBe amount
            response.category shouldBe category
            response.date shouldBe date
            response.description shouldBe description
            response.userId shouldBe userId
        }
    }
})
