package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.AddAndConfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class ScheduleNextPaymentUseCase @Inject constructor(
    private val calculator: PaymentDateCalculator,
    private val addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase,
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

        addAndConfigurePaymentUseCase(nextPayment)

        repository.updatePayment(currentPayment.copy(isNextPaymentScheduled = true))
    }

}
