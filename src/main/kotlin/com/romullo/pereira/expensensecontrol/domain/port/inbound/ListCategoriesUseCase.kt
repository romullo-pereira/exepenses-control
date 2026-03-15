package com.romullo.pereira.expensensecontrol.domain.port.inbound

interface ListCategoriesUseCase {
    fun listByUser(userId: String): List<String>
}
