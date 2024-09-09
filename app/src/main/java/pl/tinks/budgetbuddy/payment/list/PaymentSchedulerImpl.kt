package pl.tinks.budgetbuddy.payment.list

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.NextPaymentRequestDao
import pl.tinks.budgetbuddy.payment.NextPaymentRequest
import pl.tinks.budgetbuddy.payment.NotificationRequest
import pl.tinks.budgetbuddy.payment.NotificationRequestDao
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentNotificationWorker
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PaymentSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val nextPaymentRequestDao: NextPaymentRequestDao,
    private val notificationRequestDao: NotificationRequestDao,
) : PaymentScheduler {

    override suspend fun scheduleRecurringPayment(payment: Payment) {

        scheduleNotification(payment)

        if (payment.frequency == PaymentFrequency.SINGLE_PAYMENT) return

        val paymentDelay = calculatePaymentDelay(payment)

        val id = payment.id.toString()

        val paymentWorkRequest =
            OneTimeWorkRequestBuilder<RecurringPaymentWorker>().setInitialDelay(
                paymentDelay, TimeUnit.SECONDS
            ).setInputData(workDataOf("paymentId" to id)).build()

        workManager.enqueueUniqueWork(
            "Payment_$id", ExistingWorkPolicy.KEEP, paymentWorkRequest
        )

        nextPaymentRequestDao.addNextPaymentRequest(
            NextPaymentRequest(
                paymentWorkRequest.id, payment.id
            )
        )
    }

    private suspend fun scheduleNotification(payment: Payment) {

        if (!payment.notificationEnabled) return

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
                "Notification_$id", ExistingWorkPolicy.REPLACE, notificationWorkRequest
            )

            notificationRequestDao.addNotificationRequest(
                NotificationRequest(
                    notificationWorkRequest.id, payment.id
                )
            )
        }
    }

    override suspend fun cancelUpcomingPayment(payment: Payment) {
        val nextPaymentRequest = nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)
        val notificationRequest: NotificationRequest? =
            notificationRequestDao.getNotificationRequestByPaymentId(payment.id)

        workManager.cancelWorkById(nextPaymentRequest.requestId)
        nextPaymentRequestDao.deleteNextPaymentRequest(nextPaymentRequest)

        if (notificationRequest != null) {
            workManager.cancelWorkById(notificationRequest.requestId)
            notificationRequestDao.deleteNotificationRequest(notificationRequest)
        }
    }

    override suspend fun updateRecurringPayment(updatedPayment: Payment, originalPayment: Payment) {

        val hasNotificationChanged =
            updatedPayment.notificationEnabled != originalPayment.notificationEnabled
        val hasDateChanged = updatedPayment.date != originalPayment.date
        val hasTitleChanged = updatedPayment.title != originalPayment.title

        if (hasNotificationChanged || hasTitleChanged) {
            updateNotificationWorker(updatedPayment)
        }

        if (hasDateChanged) {
            updatePaymentWorker(updatedPayment)

            if (updatedPayment.notificationEnabled) {
                updateNotificationWorker(updatedPayment)
            }
        }
    }

    private suspend fun updatePaymentWorker(payment: Payment) {
        val nextPaymentRequest = nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)

        val updatedPaymentWorkRequest =
            OneTimeWorkRequestBuilder<RecurringPaymentWorker>().setInitialDelay(
                    calculatePaymentDelay(payment),
                    TimeUnit.SECONDS
                ).setInputData(workDataOf("paymentId" to payment.id.toString()))
                .setId(nextPaymentRequest.requestId).build()

        workManager.updateWork(updatedPaymentWorkRequest)
    }

    private suspend fun updateNotificationWorker(payment: Payment) {
        val notificationRequest: NotificationRequest? =
            notificationRequestDao.getNotificationRequestByPaymentId(payment.id)

        if (payment.notificationEnabled) {
            val notificationDelay = calculateNotificationDelay(payment)

            if (notificationRequest != null) {
                val updatedNotificationWorkRequest =
                    OneTimeWorkRequestBuilder<PaymentNotificationWorker>().setInitialDelay(
                            notificationDelay,
                            TimeUnit.SECONDS
                        ).setInputData(
                            workDataOf(
                                PaymentNotificationWorker.PAYMENT_TITLE_KEY to payment.title,
                                PaymentNotificationWorker.PAYMENT_ID_KEY to payment.id.toString()
                            )
                        ).setId(notificationRequest.requestId).build()

                workManager.updateWork(updatedNotificationWorkRequest)
            } else {
                scheduleNotification(payment)
            }
        } else {
            if (notificationRequest != null) {
                workManager.cancelWorkById(notificationRequest.requestId)
                notificationRequestDao.deleteNotificationRequest(notificationRequest)
            }
        }
    }


    private fun calculatePaymentDelay(payment: Payment): Long {
        val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val nextPaymentDateEpochSeconds = payment.date.toEpochSecond(ZoneOffset.UTC)
        return nextPaymentDateEpochSeconds - nowEpochSeconds
    }

    private fun calculateNotificationDelay(payment: Payment): Long {
        val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val notificationTimeEpochSeconds =
            payment.date.minusDays(1).with(LocalTime.NOON).toEpochSecond(ZoneOffset.UTC)
        return notificationTimeEpochSeconds - nowEpochSeconds
    }

}
