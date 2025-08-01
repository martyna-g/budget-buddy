package pl.tinks.budgetbuddy.shopping

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import pl.tinks.budgetbuddy.Result
import java.util.UUID
import javax.inject.Singleton

@Singleton
class ShoppingRepositoryImpl(
    private val shoppingDao: ShoppingDao, private val dispatcher: CoroutineDispatcher
) : ShoppingRepository {

    override fun getAllShoppingItems(): Flow<Result<List<ShoppingItem>>> {
        return shoppingDao.getAllShoppingItems().flowOn(dispatcher).map { shoppingList ->
            Result.Success(shoppingList) as Result<List<ShoppingItem>>
        }.catch { e ->
            emit(Result.Error(e))
        }
    }

    override fun getShoppingItemsByInBasketStatus(inBasket: Boolean): Flow<Result<List<ShoppingItem>>> {
        return shoppingDao.getShoppingItemsByInBasketStatus(inBasket).map { shoppingList ->
            Result.Success(shoppingList) as Result<List<ShoppingItem>>
        }.catch { e ->
            emit(Result.Error(e))
        }
    }

    override suspend fun getShoppingItemById(id: UUID): ShoppingItem {
        return shoppingDao.getShoppingItemById(id)
    }

    override suspend fun addShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.addShoppingItem(shoppingItem)
    }

    override suspend fun updateShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.updateShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItems(items: List<ShoppingItem>) {
        shoppingDao.deleteShoppingItems(items)
    }

    override suspend fun deleteShoppingItemsByInBasketStatus(inBasket: Boolean) {
        shoppingDao.deleteShoppingItemsByInBasketStatus(inBasket)
    }

    override suspend fun deleteAllShoppingItems() {
        shoppingDao.deleteAllShoppingItems()
    }
}
