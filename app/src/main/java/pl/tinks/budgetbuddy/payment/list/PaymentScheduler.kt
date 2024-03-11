package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment

interface PaymentScheduler {

    suspend fun scheduleRecurringPayment(payment: Payment)

    suspend fun cancelUpcomingPayment(payment: Payment)

    suspend fun updateRecurringPayment(payment: Payment)

}
