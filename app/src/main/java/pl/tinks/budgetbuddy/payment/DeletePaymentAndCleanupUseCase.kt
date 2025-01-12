package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class DeletePaymentAndCleanupUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler
) {
    suspend operator fun invoke(payment: Payment) {
        paymentRepository.deletePayment(payment)
        paymentScheduler.cancelUpcomingPayment(payment)
    }
}
