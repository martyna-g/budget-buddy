package pl.tinks.budgetbuddy.payment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentSchedulerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PaymentSchedulerModule {

    @Singleton
    @Binds
    abstract fun bindPaymentScheduler(paymentScheduler: PaymentSchedulerImpl) : PaymentScheduler
}
