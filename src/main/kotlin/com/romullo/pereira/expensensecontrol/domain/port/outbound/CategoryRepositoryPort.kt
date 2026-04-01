package com.romullo.pereira.expensensecontrol.domain.port.outbound

interface CategoryRepositoryPort {
    fun addCategory(userId: String, name: String)
    fun findByUserId(userId: String): List<String>
    fun existsByUserIdAndName(userId: String, name: String): Boolean
}
