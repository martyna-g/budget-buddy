package pl.tinks.budgetbuddy.payment.list

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.PaymentFrequency
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
        if (payment.frequency == PaymentFrequency.SINGLE_PAYMENT) return

        val paymentDelay = calculatePaymentDelay(payment)

        val id = payment.id.toString()

        val paymentWorkRequest =
            OneTimeWorkRequestBuilder<RecurringPaymentWorker>().setInitialDelay(
                paymentDelay, TimeUnit.SECONDS
            ).setInputData(workDataOf(RecurringPaymentWorker.PAYMENT_ID_KEY to id)).build()

        workManager.enqueueUniqueWork(
            "Payment_$id", ExistingWorkPolicy.KEEP, paymentWorkRequest
        )

        nextPaymentRequestDao.addNextPaymentRequest(
            NextPaymentRequest(
                paymentWorkRequest.id, payment.id
            )
        )
    }

    override suspend fun updateRecurringPayment(payment: Payment) {
        val nextPaymentRequest = nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)

        if (nextPaymentRequest != null) {
            if (payment.frequency != PaymentFrequency.SINGLE_PAYMENT) {
                val updatedPaymentRequest =
                    OneTimeWorkRequestBuilder<RecurringPaymentWorker>().setInitialDelay(
                        calculatePaymentDelay(payment), TimeUnit.SECONDS
                    ).setInputData(
                        workDataOf(RecurringPaymentWorker.PAYMENT_ID_KEY to payment.id.toString())
                    ).setId(nextPaymentRequest.requestId).build()

                workManager.updateWork(updatedPaymentRequest)
            } else {
                cancelRecurringPayment(payment)
            }
        }
    }

    override suspend fun cancelRecurringPayment(payment: Payment) {
        val nextPaymentRequest = nextPaymentRequestDao.getNextPaymentRequestByPaymentId(payment.id)

        if (nextPaymentRequest != null) {
            workManager.cancelWorkById(nextPaymentRequest.requestId)
            nextPaymentRequestDao.deleteNextPaymentRequest(nextPaymentRequest)
        }
    }

    private fun calculatePaymentDelay(payment: Payment): Long {
        val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val nextPaymentDateEpochSeconds = payment.date.toEpochSecond(ZoneOffset.UTC)
        return nextPaymentDateEpochSeconds - nowEpochSeconds
    }

}
