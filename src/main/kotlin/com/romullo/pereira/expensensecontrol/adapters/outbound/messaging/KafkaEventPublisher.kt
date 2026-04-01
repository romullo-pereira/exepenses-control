package com.romullo.pereira.expensensecontrol.adapters.outbound.messaging

import com.romullo.pereira.expensensecontrol.domain.model.event.BankTransactionsImportedEvent
import com.romullo.pereira.expensensecontrol.domain.port.outbound.EventPublisherPort
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseCreatedEvent
import com.romullo.pereira.expensensecontrol.domain.model.event.ExpenseHighAlertEvent
import org.springframework.stereotype.Component

// TODO: Implement Kafka producer in task 9
@Component
class KafkaEventPublisher : EventPublisherPort {

    override fun publishExpenseCreated(event: ExpenseCreatedEvent) {
        // TODO: implement
    }

    override fun publishExpenseHighAlert(event: ExpenseHighAlertEvent) {
        // TODO: implement
    }

    override fun publishBankTransactionsImported(event: BankTransactionsImportedEvent) {
        // TODO: implement
    }
}
