package com.romullo.pereira.expensensecontrol.domain.exception

import java.lang.RuntimeException

class DuplicatedEmailException(
    override val message: String? = DefaultMessages.DUPLICATED_EMAIL,
) : RuntimeException()
