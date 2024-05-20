package pl.tinks.budgetbuddy.payment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.UUID

@Dao
interface NotificationRequestDao {

    @Query("SELECT * FROM notification_requests WHERE paymentId = :paymentId")
    suspend fun getNotificationRequestByPaymentId(paymentId: UUID): NotificationRequest

    @Insert
    suspend fun addNotificationRequest(notificationRequest: NotificationRequest)

    @Delete
    suspend fun deleteNotificationRequest(notificationRequest: NotificationRequest)

}
