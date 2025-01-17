package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment

interface PaymentScheduler {

    suspend fun scheduleRecurringPayment(payment: Payment)

    suspend fun cancelRecurringPayment(payment: Payment)

    suspend fun updateRecurringPayment(payment: Payment)

}
