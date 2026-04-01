package com.romullo.pereira.expensensecontrol.adapters.outbound.banking

import com.romullo.pereira.expensensecontrol.domain.port.outbound.BankApiClientPort
import com.romullo.pereira.expensensecontrol.domain.port.outbound.BankTransaction
import org.springframework.stereotype.Component

// TODO: Implement bank API client in task 10
@Component
class BankApiClientAdapter : BankApiClientPort {

    override fun fetchTransactions(userId: String): List<BankTransaction> {
        // TODO: implement
        return emptyList()
    }
}
