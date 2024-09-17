package pl.tinks.budgetbuddy.payment

import javax.inject.Inject

class UndoMoveToHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend fun undoMoveToHistory(payment: Payment) {
        paymentRepository.updatePayment(payment.copy(paymentCompleted = false))
    }
}
