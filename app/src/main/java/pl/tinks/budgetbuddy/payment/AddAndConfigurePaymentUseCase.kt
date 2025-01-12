package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class AddAndConfigurePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler
) {
    suspend operator fun invoke(payment: Payment) {
        paymentRepository.addPayment(payment)
        paymentScheduler.scheduleRecurringPayment(payment)
    }
}
