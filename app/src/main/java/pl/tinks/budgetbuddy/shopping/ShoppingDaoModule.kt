package pl.tinks.budgetbuddy.shopping

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.tinks.budgetbuddy.BudgetBuddyDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ShoppingDaoModule {

    @Singleton
    @Provides
    fun provideShoppingDao(budgetBuddyDatabase: BudgetBuddyDatabase): ShoppingDao {
        return budgetBuddyDatabase.getShoppingDao()
    }

}
