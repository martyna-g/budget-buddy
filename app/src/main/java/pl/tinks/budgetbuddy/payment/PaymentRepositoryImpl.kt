package pl.tinks.budgetbuddy.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pl.tinks.budgetbuddy.Result
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(private val paymentDao: PaymentDao) :
    PaymentRepository {

    override fun getAllPayments(): Flow<Result<List<Payment>>> {
        return paymentDao.getAllPayments().map { list ->
            Result.Success(list) as Result<List<Payment>>
        }.catch { e ->
            emit(Result.Error(e))
        }
    }

    override suspend fun getPaymentById(paymentId: UUID) = paymentDao.getPaymentById(paymentId)

    override suspend fun addPayment(payment: Payment) {
        paymentDao.addPayment(payment)
    }

    override suspend fun deletePayment(payment: Payment) {
        paymentDao.deletePayment(payment)
    }

}
