package com.romullo.pereira.expensensecontrol.adapters.inbound.rest.handler

import com.romullo.pereira.expensensecontrol.domain.exception.DuplicatedEmailException
import com.romullo.pereira.expensensecontrol.domain.exception.InvalidUserException
import com.romullo.pereira.expensensecontrol.domain.model.error.BusinessError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException::class)
    fun handleDuplicatedEmail(e: DuplicatedEmailException): ResponseEntity<BusinessError> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            BusinessError(HttpStatus.CONFLICT.value(), e.message!!),
        )

    @ExceptionHandler(InvalidUserException::class)
    fun handleInvalidUser(e: InvalidUserException): ResponseEntity<BusinessError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            BusinessError(HttpStatus.UNAUTHORIZED.value(), e.message!!),
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<BusinessError> =
        ResponseEntity.badRequest().body(
            BusinessError(HttpStatus.BAD_REQUEST.value(), e.message!!),
        )

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<BusinessError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            BusinessError(HttpStatus.NOT_FOUND.value(), e.message ?: "Not found"),
        )
}
