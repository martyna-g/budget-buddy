package pl.tinks.budgetbuddy.payment.list

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.NextPaymentRequestDao
import pl.tinks.budgetbuddy.payment.NextPaymentRequest
import pl.tinks.budgetbuddy.payment.Payment
import java.time.LocalDateTime
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

            val delay = nextPaymentDateEpochSeconds - nowEpochSeconds
            val id = payment.id.toString()

            val workRequest = OneTimeWorkRequestBuilder<RecurringPaymentWorker>()
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setInputData(workDataOf("paymentId" to id))
                .build()

            nextPaymentRequestDao.addNextPaymentRequest(
                NextPaymentRequest(
                    workRequest.id,
                    payment.id
                )
            )

            workManager.enqueue(workRequest)
        }
    }

    override suspend fun cancelUpcomingPayment(payment: Payment) {
        if (payment.frequency == PaymentFrequency.SINGLE_PAYMENT) return
        val nextPaymentRequest = nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)
        workManager.cancelWorkById(nextPaymentRequest.requestId)
        nextPaymentRequestDao.deleteNextPaymentRequest(nextPaymentRequest)
    }

    override suspend fun updateRecurringPayment(payment: Payment) {
        cancelUpcomingPayment(payment)
        scheduleRecurringPayment(payment)
    }

}
