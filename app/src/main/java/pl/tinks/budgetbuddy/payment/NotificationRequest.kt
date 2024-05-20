package pl.tinks.budgetbuddy.payment

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notification_requests")
data class NotificationRequest(
    @PrimaryKey val requestId: UUID,
    val paymentId: UUID,
)
