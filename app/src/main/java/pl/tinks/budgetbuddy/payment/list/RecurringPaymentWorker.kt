package pl.tinks.budgetbuddy.payment.list

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RecurringPaymentWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
