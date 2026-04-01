package com.romullo.pereira.expensensecontrol.domain.port.inbound

import com.romullo.pereira.expensensecontrol.domain.model.bank.ImportResult

interface ImportBankTransactionsUseCase {
    fun import(userId: String): ImportResult
}
