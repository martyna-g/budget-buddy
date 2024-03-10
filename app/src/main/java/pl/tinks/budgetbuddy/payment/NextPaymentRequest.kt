package pl.tinks.budgetbuddy.payment

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "next_payment_requests")
data class NextPaymentRequest(
    @PrimaryKey val requestId: UUID,
    val paymentId: UUID,
)
