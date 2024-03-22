package pl.tinks.budgetbuddy

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.tinks.budgetbuddy.payment.NextPaymentRequest
import pl.tinks.budgetbuddy.payment.NextPaymentRequestDao
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.PaymentRoomConverters
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingItem

@Database(entities = [Payment::class, NextPaymentRequest::class, ShoppingItem::class], version = 1)
@TypeConverters(PaymentRoomConverters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun getPaymentDao(): PaymentDao
    abstract fun getNextPaymentRequestDao(): NextPaymentRequestDao
    abstract fun getShoppingDao(): ShoppingDao
}
