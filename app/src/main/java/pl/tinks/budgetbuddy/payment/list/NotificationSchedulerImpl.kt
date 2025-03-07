package pl.tinks.budgetbuddy.payment.list

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.NotificationRequest
import pl.tinks.budgetbuddy.payment.NotificationRequestDao
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentNotificationWorker
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager, private val notificationRequestDao: NotificationRequestDao
) : NotificationScheduler {

    override suspend fun scheduleNotification(payment: Payment) {
        if (!payment.notificationEnabled) {
            return
        }

        val id = payment.id.toString()

        val notificationDelay = calculateNotificationDelay(payment)

        val notificationWorkRequest = if (notificationDelay > 0) {
            OneTimeWorkRequestBuilder<PaymentNotificationWorker>().setInitialDelay(
                notificationDelay, TimeUnit.SECONDS
            ).setInputData(
                workDataOf(
                    PaymentNotificationWorker.PAYMENT_TITLE_KEY to payment.title,
                    PaymentNotificationWorker.PAYMENT_ID_KEY to id
                )
            ).build()
        } else {
            null
        }

        if (notificationWorkRequest != null) {
            workManager.enqueueUniqueWork(
                "Notification_$id", ExistingWorkPolicy.KEEP, notificationWorkRequest
            )

            notificationRequestDao.addNotificationRequest(
                NotificationRequest(
                    notificationWorkRequest.id, payment.id
                )
            )
        }
    }

    override suspend fun updateNotification(payment: Payment) {
        val notificationRequest: NotificationRequest? =
            notificationRequestDao.getNotificationRequestByPaymentId(payment.id)

        val notificationDelay = calculateNotificationDelay(payment)

        if (notificationRequest != null && notificationDelay > 0) {
            val updatedNotificationWorkRequest =
                OneTimeWorkRequestBuilder<PaymentNotificationWorker>().setInitialDelay(
                    notificationDelay, TimeUnit.SECONDS
                ).setInputData(
                    workDataOf(
                        PaymentNotificationWorker.PAYMENT_TITLE_KEY to payment.title,
                        PaymentNotificationWorker.PAYMENT_ID_KEY to payment.id.toString()
                    )
                ).setId(notificationRequest.requestId).build()

            workManager.updateWork(updatedNotificationWorkRequest)
        } else if (notificationDelay > 0) {
            scheduleNotification(payment)
        } else if (notificationRequest != null) {
            cancelNotification(payment)
        }
    }

    override suspend fun cancelNotification(payment: Payment) {
        val notificationRequest: NotificationRequest? =
            notificationRequestDao.getNotificationRequestByPaymentId(payment.id)

        if (notificationRequest != null) {
            workManager.cancelWorkById(notificationRequest.requestId)
            notificationRequestDao.deleteNotificationRequest(notificationRequest)
        }
    }

    private fun calculateNotificationDelay(payment: Payment): Long {
        val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val notificationTimeEpochSeconds =
            payment.date.minusDays(1).with(LocalTime.NOON).toEpochSecond(ZoneOffset.UTC)
        return notificationTimeEpochSeconds - nowEpochSeconds
    }
}
