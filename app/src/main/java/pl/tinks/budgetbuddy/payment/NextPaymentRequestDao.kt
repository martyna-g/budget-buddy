package pl.tinks.budgetbuddy.payment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.UUID

@Dao
interface NextPaymentRequestDao {

    @Query("SELECT * FROM next_payment_requests WHERE paymentId = :paymentId")
    suspend fun getNextPaymentRequestByPaymentId(paymentId: UUID): NextPaymentRequest?

    @Insert
    suspend fun addNextPaymentRequest(nextPaymentRequest: NextPaymentRequest)

    @Delete
    suspend fun deleteNextPaymentRequest(nextPaymentRequest: NextPaymentRequest)
}
