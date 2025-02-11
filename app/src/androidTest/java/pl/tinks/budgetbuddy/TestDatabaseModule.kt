package pl.tinks.budgetbuddy

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.PaymentDaoModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [BudgetBuddyDatabaseModule::class, PaymentDaoModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): BudgetBuddyDatabase {
        return Room.inMemoryDatabaseBuilder(context, BudgetBuddyDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @Provides
    fun providePaymentDao(database: BudgetBuddyDatabase): PaymentDao = database.getPaymentDao()
}
