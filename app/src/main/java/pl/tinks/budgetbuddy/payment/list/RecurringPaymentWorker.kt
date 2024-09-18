package pl.tinks.budgetbuddy.payment.list

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@HiltWorker
class RecurringPaymentWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PaymentRepository,
    private val calculator: PaymentDateCalculator,
    private val paymentScheduler: PaymentScheduler

) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val PAYMENT_ID_KEY = "paymentId"
    }

    override suspend fun doWork(): Result {

        val input = inputData.getString(PAYMENT_ID_KEY)

        val id = UUID.fromString(input)

        val payment = repository.getPaymentById(id)

        if (payment.isNextPaymentScheduled) return Result.success()

        val nextPaymentDate = calculator.calculateNextPaymentDate(
            payment.date.toLocalDate(), payment.frequency
        )

        val newPaymentId = UUID.randomUUID()
        val newPaymentDate = LocalDateTime.of(nextPaymentDate, LocalTime.MIDNIGHT)

        val newPayment = payment.copy(id = newPaymentId, date = newPaymentDate)

        var paymentAdded = false

        try {
            repository.addPayment(newPayment)
            paymentAdded = true

            paymentScheduler.scheduleRecurringPayment(newPayment)
            repository.updatePayment(payment.copy(isNextPaymentScheduled = true))

            return Result.success()

        } catch (e: CancellationException) {
            if (paymentAdded) {
                withContext(NonCancellable) {
                    repository.deletePayment(newPayment)
                }
            }
            return Result.failure()
        }
    }

}
