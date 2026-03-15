package com.romullo.pereira.expensensecontrol.adapters.inbound.rest

import com.romullo.pereira.expensensecontrol.domain.model.expense.CreateExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.port.inbound.CreateExpenseUseCase
import com.romullo.pereira.expensensecontrol.domain.port.inbound.GetExpenseByIdUseCase
import com.romullo.pereira.expensensecontrol.domain.port.inbound.ListExpensesUseCase
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config.logger
import com.romullo.pereira.expensensecontrol.adapters.inbound.rest.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/expenses")
class ExpenseController(
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val listExpensesUseCase: ListExpensesUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val jwtUtil: JwtUtil,
) {
    private val logger = logger()

    @PostMapping
    fun createExpense(
        @RequestBody expenseRequest: CreateExpenseRequest,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<ExpenseResponse> {
        logger.info("Received request to create expense")
        val userId = jwtUtil.getUserIdFromToken(token.removePrefix("Bearer "))
        return ResponseEntity.status(HttpStatus.CREATED).body(createExpenseUseCase.create(expenseRequest, userId))
    }

    @GetMapping
    fun listExpenses(@RequestHeader("Authorization") token: String): ResponseEntity<List<ExpenseResponse>> {
        val userId = jwtUtil.getUserIdFromToken(token.removePrefix("Bearer "))
        return ResponseEntity.ok(listExpensesUseCase.listByUser(userId))
    }

    @GetMapping("/{id}")
    fun getExpenseById(
        @PathVariable id: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<ExpenseResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.removePrefix("Bearer "))
        return ResponseEntity.ok(getExpenseByIdUseCase.getById(id, userId))
    }
}
