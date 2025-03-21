package pl.tinks.budgetbuddy.payment.list

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.tinks.budgetbuddy.BudgetBuddyDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NextPaymentRequestDaoModule {

    @Singleton
    @Provides
    fun provideNextPaymentRequestDao(budgetBuddyDatabase: BudgetBuddyDatabase) =
        budgetBuddyDatabase.getNextPaymentRequestDao()
}
