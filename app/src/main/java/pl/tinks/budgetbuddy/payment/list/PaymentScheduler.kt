package pl.tinks.budgetbuddy.payment.list

import java.util.UUID

interface PaymentScheduler {

    suspend fun scheduleRecurringPayment(paymentId: UUID)

    suspend fun cancelUpcomingPayments(paymentId: UUID)

    suspend fun updateRecurringPayment(paymentId: UUID)

}
