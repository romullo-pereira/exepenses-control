package com.romullo.pereira.expensensecontrol.domain.port.inbound

interface ImportBankTransactionsUseCase {
    fun import(userId: String): ImportResult
}

data class ImportResult(val imported: Int, val skipped: Int)
