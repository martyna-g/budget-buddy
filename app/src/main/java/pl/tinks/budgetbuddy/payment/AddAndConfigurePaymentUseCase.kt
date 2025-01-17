package pl.tinks.budgetbuddy.payment

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import pl.tinks.budgetbuddy.payment.list.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import javax.inject.Inject

class AddAndConfigurePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentScheduler: PaymentScheduler,
    private val notificationScheduler: NotificationScheduler,
) {

    suspend operator fun invoke(payment: Payment) {
        try {
            paymentRepository.addPayment(payment)
            paymentScheduler.scheduleRecurringPayment(payment)
            notificationScheduler.scheduleNotification(payment)
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                paymentRepository.deletePayment(payment)
            }
            throw e
        }
    }
}
