package pl.tinks.budgetbuddy

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import pl.tinks.budgetbuddy.payment.notification.PaymentNotificationPresenter
import javax.inject.Inject

@HiltAndroidApp
class BudgetBuddyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var paymentNotificationPresenter: PaymentNotificationPresenter

    override fun onCreate() {
        super.onCreate()
        paymentNotificationPresenter.createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
