package pl.tinks.budgetbuddy

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.tinks.budgetbuddy.security.DatabasePassphraseProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BudgetBuddyDatabaseModule {

    @Singleton
    @Provides
    fun provideEncryptedDatabaseFactory(
        provider: DatabasePassphraseProvider
    ): EncryptedDatabaseFactory = EncryptedDatabaseFactory(provider)

    @Singleton
    @Provides
    fun provideBudgetDatabase(
        @ApplicationContext context: Context, factory: EncryptedDatabaseFactory
    ): BudgetBuddyDatabase = factory.create(context)
}
