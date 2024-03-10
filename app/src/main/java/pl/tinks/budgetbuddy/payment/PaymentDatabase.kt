package pl.tinks.budgetbuddy.payment

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Payment::class, NextPaymentRequest::class], version = 1)
@TypeConverters(PaymentRoomConverters::class)
abstract class PaymentDatabase : RoomDatabase() {
    abstract fun getPaymentDao(): PaymentDao
    abstract fun getNextPaymentRequestDao(): NextPaymentRequestDao

}
