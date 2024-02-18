package pl.tinks.budgetbuddy.payment.list

import java.util.UUID

interface PaymentScheduler {

    suspend fun scheduleRecurringPayment(paymentId: UUID)
}
