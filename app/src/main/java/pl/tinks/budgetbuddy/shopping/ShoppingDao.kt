package pl.tinks.budgetbuddy.shopping

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ShoppingDao {

    @Query("SELECT * FROM shopping_items")
    fun getAllShoppingItems(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE in_basket LIKE :inBasket")
    fun getShoppingItemsByInBasketStatus(inBasket: Boolean): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE id LIKE :itemId")
    suspend fun getShoppingItemById(itemId: UUID): ShoppingItem

    @Insert
    suspend fun addShoppingItem(shoppingItem: ShoppingItem)

    @Update
    suspend fun updateShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItems(items: List<ShoppingItem>)

    @Query("DELETE FROM shopping_items WHERE in_basket = :inBasket")
    suspend fun deleteShoppingItemsByInBasketStatus(inBasket: Boolean)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAllShoppingItems()

}
