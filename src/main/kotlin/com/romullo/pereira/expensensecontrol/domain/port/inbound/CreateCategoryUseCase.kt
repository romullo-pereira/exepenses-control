package com.romullo.pereira.expensensecontrol.domain.port.inbound

interface CreateCategoryUseCase {
    fun create(name: String, userId: String)
}
