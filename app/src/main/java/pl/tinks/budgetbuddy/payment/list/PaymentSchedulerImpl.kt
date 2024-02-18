package pl.tinks.budgetbuddy.payment.list

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PaymentSchedulerImpl @Inject constructor(
    private val repository: PaymentRepository,
    private val workManager: WorkManager
) : PaymentScheduler {

    override suspend fun scheduleRecurringPayment(paymentId: UUID) {

        val payment = repository.getPaymentById(paymentId)

        val nextPaymentDateEpochSeconds = payment.date.toEpochSecond(ZoneOffset.UTC)
        val nowEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        val delay = nextPaymentDateEpochSeconds - nowEpochSeconds
        val id = paymentId.toString()

        val workRequest = OneTimeWorkRequestBuilder<RecurringPaymentWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(workDataOf("paymentId" to id))
            .build()

        workManager.enqueue(workRequest)
    }
}
