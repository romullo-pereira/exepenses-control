package com.romullo.pereira.expensensecontrol.domain.model.category

import jakarta.validation.constraints.NotBlank

data class CreateCategoryRequest(
    @field:NotBlank
    val name: String,
)
