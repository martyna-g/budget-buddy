package pl.tinks.budgetbuddy.payment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE id LIKE :paymentId")
    suspend fun getPaymentById(paymentId: UUID): Payment

    @Insert
    suspend fun addPayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

}
