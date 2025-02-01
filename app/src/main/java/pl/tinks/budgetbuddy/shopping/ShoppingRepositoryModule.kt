package pl.tinks.budgetbuddy.shopping

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ShoppingRepositoryModule {

    @Singleton
    @Provides
    fun provideShoppingRepository(shoppingDao: ShoppingDao): ShoppingRepository =
        ShoppingRepositoryImpl(shoppingDao, Dispatchers.IO)
}
