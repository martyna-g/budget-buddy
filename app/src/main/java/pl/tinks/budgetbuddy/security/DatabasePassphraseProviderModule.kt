package pl.tinks.budgetbuddy.security

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabasePassphraseProviderModule {

    @Binds
    @Singleton
    abstract fun bindDatabasePassphraseProvider(
        defaultDatabasePassphraseProvider: DefaultDatabasePassphraseProvider
    ): DatabasePassphraseProvider
}
