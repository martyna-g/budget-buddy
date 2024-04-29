package pl.tinks.budgetbuddy.payment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.money.Money
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey val id: UUID,
    val title: String,
    val amount: Money,
    val date: LocalDateTime,
    val frequency: PaymentFrequency,
    @ColumnInfo(name = "payment_completed") var paymentCompleted: Boolean = false,
)
