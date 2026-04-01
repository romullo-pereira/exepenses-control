package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.user.RegisterRequest
import com.romullo.pereira.expensensecontrol.domain.model.user.User

interface RegisterUserUseCase {
    fun register(request: RegisterRequest): User
}
