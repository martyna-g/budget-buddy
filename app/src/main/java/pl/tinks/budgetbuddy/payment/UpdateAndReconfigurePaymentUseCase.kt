package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class UpdateAndReconfigurePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler
) {

    suspend operator fun invoke(payment: Payment) {

        val originalPayment = paymentRepository.getPaymentById(payment.id)
            ?: throw NullPointerException("Payment with ID ${payment.id} not found.")

        paymentRepository.updatePayment(payment)

        val hasDateChanged = payment.date != originalPayment.date
        val hasNotificationChanged = payment.notificationEnabled != originalPayment.notificationEnabled
        val hasTitleChanged = payment.title != originalPayment.title

        if (hasDateChanged) {
            paymentScheduler.updateRecurringPayment(payment)
        }

        if (hasNotificationChanged || hasTitleChanged || hasDateChanged) {
            if (payment.notificationEnabled) {
                paymentScheduler.updateNotification(payment)
            } else {
                paymentScheduler.cancelNotification(payment)
            }
        }

    }
}
