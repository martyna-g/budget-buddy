package pl.tinks.budgetbuddy.payment

import kotlinx.coroutines.flow.Flow
import pl.tinks.budgetbuddy.Result
import java.util.UUID

interface PaymentRepository {

    fun getAllPayments(): Flow<Result<List<Payment>>>

    suspend fun getPaymentById(paymentId: UUID): Payment?

    suspend fun addPayment(payment: Payment)

    suspend fun updatePayment(payment: Payment)

    suspend fun deletePayment(payment: Payment)

    suspend fun deletePayments(payments: List<Payment>)

}
