package com.romullo.pereira.expensensecontrol.domain.port.outbound

import com.romullo.pereira.expensensecontrol.domain.model.event.BankTransactionsImportedEvent
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseCreatedEvent
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseHighAlertEvent

interface EventPublisherPort {
    fun publishExpenseCreated(event: ExpenseCreatedEvent)
    fun publishExpenseHighAlert(event: ExpenseHighAlertEvent)
    fun publishBankTransactionsImported(event: BankTransactionsImportedEvent)
}
