package pl.tinks.budgetbuddy.shopping

import kotlinx.coroutines.flow.Flow
import pl.tinks.budgetbuddy.Result
import java.util.UUID

interface ShoppingRepository {

    fun getAllShoppingItems(): Flow<Result<List<ShoppingItem>>>

    fun getShoppingItemsByInBasketStatus(inBasket: Boolean): Flow<Result<List<ShoppingItem>>>

    suspend fun getShoppingItemById(id: UUID): ShoppingItem

    suspend fun addShoppingItem(shoppingItem: ShoppingItem)

    suspend fun updateShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

}
