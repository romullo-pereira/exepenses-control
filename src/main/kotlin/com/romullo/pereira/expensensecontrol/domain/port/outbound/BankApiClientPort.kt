package com.romullo.pereira.expensensecontrol.domain.port.outbound

import java.math.BigDecimal
import java.time.ZonedDateTime

data class BankTransaction(
    val externalId: String,
    val amount: BigDecimal,
    val date: ZonedDateTime,
    val description: String
)

interface BankApiClientPort {
    fun fetchTransactions(userId: String): List<BankTransaction>
}
