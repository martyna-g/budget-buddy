package pl.tinks.budgetbuddy.payment

import java.util.UUID
import javax.inject.Inject

class GetPaymentByIdUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend fun getPaymentById(paymentId: UUID): Payment {
        return paymentRepository.getPaymentById(paymentId)
            ?: throw NullPointerException("Payment with ID $paymentId not found.")
    }
}
