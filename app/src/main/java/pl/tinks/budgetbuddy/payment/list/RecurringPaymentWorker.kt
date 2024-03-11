package pl.tinks.budgetbuddy.payment.list

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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

    override suspend fun doWork(): Result {

        val input = inputData.getString("paymentId")
        val id = UUID.fromString(input)

        val payment = repository.getPaymentById(id)

        val nextPaymentDate = calculator.calculateNextPaymentDate(
            payment.date.toLocalDate(),
            payment.frequency
        )

        val newPaymentId = UUID.randomUUID()
        val newPaymentDate = LocalDateTime.of(nextPaymentDate, LocalTime.MIDNIGHT)

        val newPayment = payment.copy(id = newPaymentId, date = newPaymentDate)

        repository.addPayment(newPayment)
        paymentScheduler.scheduleRecurringPayment(newPayment)

        return Result.success()
    }
}
