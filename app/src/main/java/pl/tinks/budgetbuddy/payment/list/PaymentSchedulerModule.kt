package pl.tinks.budgetbuddy.payment.list

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PaymentSchedulerModule {

    @Singleton
    @Binds
    abstract fun bindPaymentScheduler(paymentScheduler: PaymentSchedulerImpl) : PaymentScheduler
}
