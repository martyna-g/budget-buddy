package pl.tinks.budgetbuddy.payment.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PaymentNotificationPresenterModule {

    @Singleton
    @Provides
    fun providePaymentNotificationPresenter(@ApplicationContext context: Context): PaymentNotificationPresenter {
        return PaymentNotificationPresenter(context)
    }
}
