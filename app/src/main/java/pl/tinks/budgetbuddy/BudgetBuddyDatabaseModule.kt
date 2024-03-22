package pl.tinks.budgetbuddy

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
object BudgetBuddyDatabaseModule {

    @Singleton
    @Provides
    fun provideBudgetDatabase(@ApplicationContext context: Context): BudgetBuddyDatabase {
        return Room.databaseBuilder(
            context, BudgetBuddyDatabase::class.java, "budget_buddy_database"
        ).build()
    }
}
