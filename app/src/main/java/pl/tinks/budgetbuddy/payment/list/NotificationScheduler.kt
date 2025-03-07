package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment

interface NotificationScheduler {

    suspend fun scheduleNotification(payment: Payment)

    suspend fun updateNotification(payment: Payment)

    suspend fun cancelNotification(payment: Payment)
}
