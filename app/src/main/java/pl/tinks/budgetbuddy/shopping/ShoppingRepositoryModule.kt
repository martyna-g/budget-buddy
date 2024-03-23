package pl.tinks.budgetbuddy.shopping

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class ShoppingRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindShoppingRepository(shoppingRepository: ShoppingRepositoryImpl): ShoppingRepository
}
