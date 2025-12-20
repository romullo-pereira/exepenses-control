package com.romullo.pereira.expensensecontrol.infrastructure.controller

import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseRequest
import com.romullo.pereira.expensensecontrol.domain.model.expense.ExpenseResponse
import com.romullo.pereira.expensensecontrol.domain.service.ExpenseService
import com.romullo.pereira.expensensecontrol.infrastructure.config.logger
import org.apache.el.parser.Token
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/expenses")
class ExpenseController(
    private val service: ExpenseService
) {

    private val logger = logger()

    @PostMapping
    fun createExpense(
        @RequestBody
        expenseRequest: ExpenseRequest,
        @RequestHeader("Authorization") token: String
    ) : ResponseEntity<ExpenseResponse> {
        logger.info("Received request to create expense")
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.createExpense(expenseRequest, token))
    }

}