package com.romullo.pereira.expensensecontrol.infrastructure.handler

import com.romullo.pereira.expensensecontrol.domain.exception.DuplicatedEmailException
import com.romullo.pereira.expensensecontrol.domain.model.error.BusinessError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException::class)
    fun handleDuplicatedEmail(e: DuplicatedEmailException): ResponseEntity<BusinessError> {
        return ResponseEntity.badRequest().body(BusinessError(HttpStatus.BAD_REQUEST.value(), e.message!!))
    }
}