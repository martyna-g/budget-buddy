package pl.tinks.budgetbuddy.payment.list

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import pl.tinks.budgetbuddy.payment.GetPaymentByIdUseCase
import java.util.UUID

@HiltWorker
class RecurringPaymentWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPaymentByIdUseCase: GetPaymentByIdUseCase,
    private val scheduleNextPaymentUseCase: ScheduleNextPaymentUseCase,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val PAYMENT_ID_KEY = "paymentId"
    }

    override suspend fun doWork(): Result {
        val input = inputData.getString(PAYMENT_ID_KEY) ?: return Result.failure()

        val paymentId = UUID.fromString(input)

        val payment = getPaymentByIdUseCase.getPaymentById(paymentId)

        if (payment.isNextPaymentScheduled) {
            return Result.success()
        }

        return try {
            scheduleNextPaymentUseCase(payment)
            Result.success()
        } catch (e: CancellationException) {
            Result.retry()
        }
    }
}
