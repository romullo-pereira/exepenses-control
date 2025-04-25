package com.romullo.pereira.expensensecontrol.infrastructure.persistence

import com.romullo.pereira.expensensecontrol.domain.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId> {

    fun findByEmail(email: String) : List<User>

}