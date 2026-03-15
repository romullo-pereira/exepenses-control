package com.romullo.pereira.expensensecontrol.domain.model.error

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.ZonedDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BusinessError(
    val status: Int,
    val message: String,
    val dateTime: ZonedDateTime = ZonedDateTime.now(),
)
