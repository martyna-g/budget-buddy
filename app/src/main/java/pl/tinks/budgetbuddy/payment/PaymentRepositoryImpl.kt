package pl.tinks.budgetbuddy.payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import pl.tinks.budgetbuddy.Result
import java.util.UUID
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl(
    private val paymentDao: PaymentDao, private val dispatcher: CoroutineDispatcher
) : PaymentRepository {

    override fun getAllPayments(): Flow<Result<List<Payment>>> {
        return paymentDao.getAllPayments().flowOn(dispatcher).map { list ->
            Result.Success(list) as Result<List<Payment>>
        }.catch { e ->
            emit(Result.Error(e))
        }
    }

    override suspend fun getPaymentById(paymentId: UUID) = paymentDao.getPaymentById(paymentId)

    override suspend fun addPayment(payment: Payment) {
        paymentDao.addPayment(payment)
    }

    override suspend fun updatePayment(payment: Payment) {
        paymentDao.updatePayment(payment)
    }

    override suspend fun deletePayment(payment: Payment) {
        paymentDao.deletePayment(payment)
    }

    override suspend fun deletePayments(payments: List<Payment>) {
        paymentDao.deletePayments(payments)
    }
}
