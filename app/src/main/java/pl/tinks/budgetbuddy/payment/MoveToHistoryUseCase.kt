package pl.tinks.budgetbuddy.payment

import javax.inject.Inject

class MoveToHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment) {
        paymentRepository.updatePayment(payment.copy(paymentCompleted = true))
    }
}
