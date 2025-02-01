package pl.tinks.budgetbuddy.payment

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PaymentRepositoryModule {

    @Singleton
    @Provides
    fun providePaymentRepository(paymentDao: PaymentDao): PaymentRepository =
        PaymentRepositoryImpl(paymentDao, Dispatchers.IO)
}
