package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.category.CategoryResponse

interface ListCategoriesUseCase {
    fun listByUser(userId: String): List<CategoryResponse>
}
