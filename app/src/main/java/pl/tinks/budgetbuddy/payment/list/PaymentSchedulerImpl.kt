package pl.tinks.budgetbuddy.payment.list

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.NextPaymentRequestDao
import pl.tinks.budgetbuddy.payment.NextPaymentRequest
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
) : PaymentScheduler {

    override suspend fun scheduleRecurringPayment(payment: Payment) {

        if (payment.frequency != PaymentFrequency.SINGLE_PAYMENT) {

            val nextPaymentDateEpochSeconds = payment.date.toEpochSecond(ZoneOffset.UTC)
            val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val notificationTimeEpochSeconds =
                payment.date.minusDays(1).with(LocalTime.NOON).toEpochSecond(ZoneOffset.UTC)

            val paymentDelay = nextPaymentDateEpochSeconds - nowEpochSeconds
            val notificationDelay = notificationTimeEpochSeconds - nowEpochSeconds

            val id = payment.id.toString()

            val paymentWorkRequest = OneTimeWorkRequestBuilder<RecurringPaymentWorker>()
                .setInitialDelay(paymentDelay, TimeUnit.SECONDS)
                .setInputData(workDataOf("paymentId" to id))
                .build()


            val notificationWorkRequest = if (notificationDelay > 0) {
                OneTimeWorkRequestBuilder<PaymentNotificationWorker>()
                    .setInitialDelay(notificationDelay, TimeUnit.SECONDS)
                    .setInputData(
                        workDataOf(
                            PaymentNotificationWorker.PAYMENT_TITLE_KEY to payment.title,
                            PaymentNotificationWorker.PAYMENT_ID_KEY to id
                        )
                    )
                    .build()
            } else {
                null
            }

            nextPaymentRequestDao.addNextPaymentRequest(
                NextPaymentRequest(
                    paymentWorkRequest.id,
                    notificationWorkRequest?.id,
                    payment.id
                )
            )

            workManager.enqueueUniqueWork(
                "Payment_$id",
                ExistingWorkPolicy.KEEP,
                paymentWorkRequest
            )

            if (notificationWorkRequest != null && payment.notificationEnabled) {
                workManager.enqueueUniqueWork(
                    "Notification_$id",
                    ExistingWorkPolicy.KEEP,
                    notificationWorkRequest
                )
            }
        }

    }

    override suspend fun cancelUpcomingPayment(payment: Payment) {
        val nextPaymentRequest: NextPaymentRequest? =
            nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)
        if (nextPaymentRequest != null) {
            workManager.cancelWorkById(nextPaymentRequest.requestId)
            nextPaymentRequestDao.deleteNextPaymentRequest(nextPaymentRequest)
        }
    }

    override suspend fun updateRecurringPayment(payment: Payment) {
        cancelUpcomingPayment(payment)
        scheduleRecurringPayment(payment)
    }

}
