package pl.tinks.budgetbuddy

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.tinks.budgetbuddy.payment.list.NextPaymentRequest
import pl.tinks.budgetbuddy.payment.list.NextPaymentRequestDao
import pl.tinks.budgetbuddy.payment.notification.NotificationRequest
import pl.tinks.budgetbuddy.payment.notification.NotificationRequestDao
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingItem

@Database(
    entities = [Payment::class, NextPaymentRequest::class, NotificationRequest::class, ShoppingItem::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverter::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun getPaymentDao(): PaymentDao
    abstract fun getNextPaymentRequestDao(): NextPaymentRequestDao
    abstract fun getNotificationRequestDao(): NotificationRequestDao
    abstract fun getShoppingDao(): ShoppingDao
}
