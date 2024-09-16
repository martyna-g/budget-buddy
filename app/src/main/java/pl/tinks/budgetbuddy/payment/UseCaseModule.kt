package pl.tinks.budgetbuddy.payment

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideUpdatePaymentUseCase(
        paymentRepository: PaymentRepository,
        paymentScheduler: PaymentScheduler
    ): UpdatePaymentUseCase {
        return UpdatePaymentUseCase(paymentRepository, paymentScheduler)
    }

}
