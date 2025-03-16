package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.notification.NotificationScheduler
import javax.inject.Inject

class DeletePaymentAndCleanupUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler,
    private val notificationScheduler: NotificationScheduler
) {

    suspend operator fun invoke(payment: Payment) {
        paymentRepository.deletePayment(payment)
        paymentScheduler.cancelRecurringPayment(payment)
        notificationScheduler.cancelNotification(payment)
    }

}
