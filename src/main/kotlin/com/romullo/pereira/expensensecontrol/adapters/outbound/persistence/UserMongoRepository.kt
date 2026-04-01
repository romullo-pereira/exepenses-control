package com.romullo.pereira.expensensecontrol.adapters.outbound.persistence

import com.romullo.pereira.expensensecontrol.domain.model.user.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserMongoRepository : MongoRepository<User, String> {
    fun findByEmail(email: String): List<User>
}
