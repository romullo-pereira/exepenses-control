package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.login.LoginRequest
import com.romullo.pereira.expensensecontrol.domain.model.login.TokenResponse

interface AuthenticateUserUseCase {
    fun authenticate(loginRequest: LoginRequest): TokenResponse
}
