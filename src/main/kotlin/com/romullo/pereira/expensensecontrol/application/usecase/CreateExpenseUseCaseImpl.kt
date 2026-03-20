package com.romullo.pereira.expensensecontrol.application.usecase

import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidInputException
import com.romullo.pereira.expensensecontrol.domain.model.enum.ExpenseSource
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseCreatedEvent
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseHighAlertEvent
import com.romullo.pereira.expensensecontrol.domain.model.expense.CreateExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.Expense
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.CreateExpenseUseCase
import com.romullo.pereira.expensensecontrol.domain.port.outbound.EventPublisherPort
import com.romullo.pereira.expensensecontrol.domain.port.outbound.ExpenseRepositoryPort
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import org.springframework.stereotype.Service

@Service
class CreateExpenseUseCaseImpl(
    private val expenseRepository: ExpenseRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val eventPublisher: EventPublisherPort,
) : CreateExpenseUseCase {

    private val logger = logger()

    override fun create(request: CreateExpenseRequest, userId: String): ExpenseResponse {
        if (request.amount <= 0) {
            throw InvalidInputException("O valor da despesa deve ser maior que zero.")
        }
        if (request.category.isBlank()) {
            throw InvalidInputException("A categoria é obrigatória.")
        }
        if (request.description.isBlank()) {
            throw InvalidInputException("A descrição é obrigatória.")
        }

        val expense = expenseRepository.save(
            Expense(
                userId = userId,
                amount = request.amount,
                category = request.category,
                date = request.date,
                description = request.description,
                source = ExpenseSource.MANUAL,
            )
        )

        try {
            eventPublisher.publishExpenseCreated(
                ExpenseCreatedEvent(
                    expenseId = expense.id,
                    userId = expense.userId,
                    amount = expense.amount,
                    category = expense.category,
                )
            )
        } catch (e: Exception) {
            logger.error("Falha ao publicar evento expense.created para despesa ${expense.id}: ${e.message}", e)
        }

        val user = userRepository.findById(userId)
        val expenseLimit = user?.expenseLimit
        if (expenseLimit != null && expense.amount > expenseLimit) {
            try {
                eventPublisher.publishExpenseHighAlert(
                    ExpenseHighAlertEvent(userId = userId, amount = expense.amount)
                )
            } catch (e: Exception) {
                logger.error("Falha ao publicar evento alert.expense.high para usuário $userId: ${e.message}", e)
            }
        }

        return expense.toResponse()
    }
}

private fun Expense.toResponse() = ExpenseResponse(
    id = id,
    userId = userId,
    amount = amount,
    category = category,
    date = date,
    description = description,
    source = source.name,
)
