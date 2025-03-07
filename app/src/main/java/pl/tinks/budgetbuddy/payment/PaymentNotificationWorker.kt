package pl.tinks.budgetbuddy.payment

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class PaymentNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val paymentNotificationPresenter: PaymentNotificationPresenter
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val PAYMENT_TITLE_KEY = "paymentTitle"
        const val PAYMENT_ID_KEY = "paymentId"
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(PAYMENT_TITLE_KEY)
        val id = inputData.getString(PAYMENT_ID_KEY)

        if (title != null && id != null) {
            paymentNotificationPresenter.sendPaymentReminderNotification(title, UUID.fromString(id))
        }
        return Result.success()
    }
}
