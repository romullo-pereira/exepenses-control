package com.romullo.pereira.expensensecontrol.domain.port.outbound

import com.romullo.pereira.expensensecontrol.domain.model.user.User

interface UserRepositoryPort {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}
