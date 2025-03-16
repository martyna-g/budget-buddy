package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import javax.inject.Inject

class MoveToHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment) {
        paymentRepository.updatePayment(payment.copy(paymentCompleted = true))
    }
}
