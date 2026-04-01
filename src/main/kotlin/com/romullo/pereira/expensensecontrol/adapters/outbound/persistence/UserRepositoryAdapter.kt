package com.romullo.pereira.expensensecontrol.adapters.outbound.persistence

import com.romullo.pereira.expensensecontrol.domain.model.user.User
import com.romullo.pereira.expensensecontrol.domain.port.outbound.UserRepositoryPort
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val userMongoRepository: UserMongoRepository,
) : UserRepositoryPort {

    override fun save(user: User): User = userMongoRepository.save(user)

    override fun findByEmail(email: String): User? =
        userMongoRepository.findByEmail(email).firstOrNull()

    override fun findById(id: String): User? =
        userMongoRepository.findById(id).orElse(null)

    override fun existsByEmail(email: String): Boolean =
        userMongoRepository.findByEmail(email).isNotEmpty()
}
