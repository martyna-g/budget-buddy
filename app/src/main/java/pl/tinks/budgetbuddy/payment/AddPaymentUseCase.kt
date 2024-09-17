package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class AddPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler
) {
    suspend fun addPayment(payment: Payment) {
        paymentRepository.addPayment(payment)
        paymentScheduler.scheduleRecurringPayment(payment)
    }
}
