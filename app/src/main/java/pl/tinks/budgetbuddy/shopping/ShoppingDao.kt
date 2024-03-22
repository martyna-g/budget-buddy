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

    @Query("SELECT * FROM shopping_items WHERE is_collected LIKE :isCollected")
    fun getItemsByCollectedStatus(isCollected: Boolean): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE id LIKE :itemId")
    suspend fun getShoppingItemById(itemId: UUID): ShoppingItem

    @Insert
    suspend fun addShoppingItem(shoppingItem: ShoppingItem)

    @Update
    suspend fun updateShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

}
