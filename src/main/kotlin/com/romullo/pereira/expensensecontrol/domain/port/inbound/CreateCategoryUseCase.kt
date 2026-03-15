package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.category.CategoryResponse

interface CreateCategoryUseCase {
    fun create(name: String, userId: String): CategoryResponse
}
