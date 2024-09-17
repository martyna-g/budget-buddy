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

    @Provides
    fun provideAddPaymentUseCase(
        paymentRepository: PaymentRepository,
        paymentScheduler: PaymentScheduler
    ): AddPaymentUseCase {
        return AddPaymentUseCase(paymentRepository, paymentScheduler)
    }

    @Provides
    fun provideDeletePaymentUseCase(
        paymentRepository: PaymentRepository,
        paymentScheduler: PaymentScheduler
    ): DeletePaymentUseCase {
        return DeletePaymentUseCase(paymentRepository, paymentScheduler)
    }

    @Provides
    fun provideMoveToHistoryUseCase(
        paymentRepository: PaymentRepository
    ): MoveToHistoryUseCase {
        return MoveToHistoryUseCase(paymentRepository)
    }

    @Provides
    fun provideUndoMoveToHistoryUseCase(
        paymentRepository: PaymentRepository
    ): UndoMoveToHistoryUseCase {
        return UndoMoveToHistoryUseCase(paymentRepository)
    }
}
