package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class DeletePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler
) {
    suspend fun deletePayment(payment: Payment) {
        paymentRepository.deletePayment(payment)
        paymentScheduler.cancelUpcomingPayment(payment)
    }
}
