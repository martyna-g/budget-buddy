package pl.tinks.budgetbuddy.payment

import javax.inject.Inject

class MoveToHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend fun moveToHistory(payment: Payment) {
        paymentRepository.updatePayment(payment.copy(paymentCompleted = true))
    }
}
