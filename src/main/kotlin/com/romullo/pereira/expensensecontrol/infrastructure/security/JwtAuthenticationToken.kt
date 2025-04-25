package com.romullo.pereira.expensensecontrol.infrastructure.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
        private val principal: String,
        private val credentials: Any?,
        authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any? = credentials

    override fun getPrincipal(): Any = principal
}