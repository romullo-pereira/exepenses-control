package com.romullo.pereira.expensensecontrol.domain.exception

import com.romullo.pereira.expensensecontrol.domain.commons.DefaultMessages
import java.lang.RuntimeException

class InvalidUserException(
    override val message: String? = DefaultMessages.INVALID_USER,
) : RuntimeException()
