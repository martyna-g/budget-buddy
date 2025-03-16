package pl.tinks.budgetbuddy.payment.list

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class ScheduleNextPaymentUseCase @Inject constructor(
    private val calculator: PaymentDateCalculator,
    private val addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase,
    private val deletePaymentAndCleanupUseCase: DeletePaymentAndCleanupUseCase,
    private val repository: PaymentRepository,
) {

    suspend operator fun invoke(currentPayment: Payment) {
        val nextPaymentId = UUID.randomUUID()

        val nextPaymentDate = LocalDateTime.of(
            calculator.calculateNextPaymentDate(
                currentPayment.date.toLocalDate(), currentPayment.frequency
            ), LocalTime.MIDNIGHT
        )

        val nextPayment = currentPayment.copy(
            id = nextPaymentId, date = nextPaymentDate
        )

        try {
            addAndConfigurePaymentUseCase(nextPayment)
            repository.updatePayment(currentPayment.copy(isNextPaymentScheduled = true))
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                deletePaymentAndCleanupUseCase(nextPayment)
            }
            throw e
        }
    }
}
