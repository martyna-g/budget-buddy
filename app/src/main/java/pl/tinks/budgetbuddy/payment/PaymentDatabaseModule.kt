package pl.tinks.budgetbuddy.payment

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PaymentDatabaseModule {

    @Singleton
    @Provides
    fun providePaymentDatabase(@ApplicationContext context: Context): PaymentDatabase {
        return Room.databaseBuilder(
            context, PaymentDatabase::class.java, "payment_database"
        ).build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object PaymentDaoModule {

    @Singleton
    @Provides
    fun providePaymentDao(paymentDatabase: PaymentDatabase) = paymentDatabase.getPaymentDao()
}

@InstallIn(SingletonComponent::class)
@Module
object NextPaymentRequestDaoModule {

    @Singleton
    @Provides
    fun provideNextPaymentRequestDao(paymentDatabase: PaymentDatabase) =
        paymentDatabase.getNextPaymentRequestDao()
}
