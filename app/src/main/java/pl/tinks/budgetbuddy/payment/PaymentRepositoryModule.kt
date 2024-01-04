package pl.tinks.budgetbuddy.payment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PaymentRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPaymentRepository(paymentRepository: PaymentRepositoryImpl): PaymentRepository
}
