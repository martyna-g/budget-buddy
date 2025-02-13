package pl.tinks.budgetbuddy

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import pl.tinks.budgetbuddy.shopping.ShoppingRepositoryImpl
import pl.tinks.budgetbuddy.shopping.ShoppingRepositoryModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [ShoppingRepositoryModule::class]
)
object TestShoppingRepositoryModule {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideShoppingRepository(shoppingDao: ShoppingDao): ShoppingRepository =
        ShoppingRepositoryImpl(shoppingDao, UnconfinedTestDispatcher())
}
