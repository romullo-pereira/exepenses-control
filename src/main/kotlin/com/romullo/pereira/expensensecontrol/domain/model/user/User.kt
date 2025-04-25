package com.romullo.pereira.expensensecontrol.domain.model.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
        @Id
        val id: ObjectId = ObjectId.get(),
        @Indexed(unique = true)
        val email: String,
        val password: String,
        val categories: Set<String>
)