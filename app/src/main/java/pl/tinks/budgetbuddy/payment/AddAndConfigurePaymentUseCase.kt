package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.notification.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class AddAndConfigurePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler,
    private val notificationScheduler: NotificationScheduler,
) {

    suspend operator fun invoke(payment: Payment) {
        paymentRepository.addPayment(payment)
        paymentScheduler.scheduleRecurringPayment(payment)
        notificationScheduler.scheduleNotification(payment)
    }
}
