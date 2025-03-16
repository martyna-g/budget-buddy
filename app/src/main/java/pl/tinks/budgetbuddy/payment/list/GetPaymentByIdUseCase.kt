package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.util.UUID
import javax.inject.Inject

class GetPaymentByIdUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(paymentId: UUID): Payment {
        return paymentRepository.getPaymentById(paymentId)
            ?: throw IllegalStateException("Payment with ID $paymentId not found. This indicates a bug in the app.")
    }
}
