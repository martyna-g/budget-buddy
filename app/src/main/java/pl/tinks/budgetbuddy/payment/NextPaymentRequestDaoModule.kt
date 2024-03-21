package pl.tinks.budgetbuddy.payment

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NextPaymentRequestDaoModule {

    @Singleton
    @Provides
    fun provideNextPaymentRequestDao(paymentDatabase: PaymentDatabase) =
        paymentDatabase.getNextPaymentRequestDao()
}
