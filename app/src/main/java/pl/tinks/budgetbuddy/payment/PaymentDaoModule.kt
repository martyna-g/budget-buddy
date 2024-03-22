package pl.tinks.budgetbuddy.payment

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.tinks.budgetbuddy.BudgetBuddyDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PaymentDaoModule {

    @Singleton
    @Provides
    fun providePaymentDao(budgetBuddyDatabase: BudgetBuddyDatabase) = budgetBuddyDatabase.getPaymentDao()
}
