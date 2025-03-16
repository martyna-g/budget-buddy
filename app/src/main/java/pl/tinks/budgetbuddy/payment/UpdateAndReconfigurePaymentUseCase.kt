package pl.tinks.budgetbuddy.payment

import pl.tinks.budgetbuddy.payment.notification.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class UpdateAndReconfigurePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler,
    private val notificationScheduler: NotificationScheduler,
) {

    suspend operator fun invoke(payment: Payment) {
        val originalPayment = paymentRepository.getPaymentById(payment.id)
            ?: throw IllegalStateException("Payment with ID ${payment.id} not found. This indicates a bug in the app.")

        paymentRepository.updatePayment(payment)

        val hasDateChanged = payment.date != originalPayment.date
        val hasNotificationChanged =
            payment.notificationEnabled != originalPayment.notificationEnabled
        val hasTitleChanged = payment.title != originalPayment.title
        val hasFrequencyChanged = payment.frequency != originalPayment.frequency

        val wasRecurring = originalPayment.frequency != PaymentFrequency.SINGLE_PAYMENT
        val isRecurring = payment.frequency != PaymentFrequency.SINGLE_PAYMENT

        if (hasFrequencyChanged) {
            when {
                wasRecurring && !isRecurring -> paymentScheduler.cancelRecurringPayment(payment)
                !wasRecurring && isRecurring -> paymentScheduler.scheduleRecurringPayment(payment)
                else -> paymentScheduler.updateRecurringPayment(payment)
            }
        } else {
            when {
                isRecurring && hasDateChanged -> paymentScheduler.updateRecurringPayment(payment)
            }
        }

        val shouldUpdateNotifications =
            hasNotificationChanged || hasDateChanged || hasTitleChanged || hasFrequencyChanged

        if (shouldUpdateNotifications) {
            when {
                payment.notificationEnabled && hasNotificationChanged -> {
                    notificationScheduler.scheduleNotification(payment)
                }

                payment.notificationEnabled -> {
                    notificationScheduler.updateNotification(payment)
                }

                hasNotificationChanged -> {
                    notificationScheduler.cancelNotification(payment)
                }
            }
        }
    }
}
