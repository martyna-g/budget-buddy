package pl.tinks.budgetbuddy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import pl.tinks.budgetbuddy.shopping.ShoppingRepositoryImpl
import java.util.UUID

class ShoppingRepositoryIntegrationTest {

    private lateinit var database: BudgetBuddyDatabase
    private lateinit var shoppingDao: ShoppingDao
    private lateinit var shoppingRepository: ShoppingRepository
    private lateinit var shoppingItem: ShoppingItem
    private val itemId = UUID.randomUUID()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), BudgetBuddyDatabase::class.java
        ).build()
        shoppingDao = database.getShoppingDao()
        shoppingRepository = ShoppingRepositoryImpl(shoppingDao, UnconfinedTestDispatcher())
        shoppingItem = ShoppingItem(itemId, "Test Item")
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllShoppingItems_emitsSuccess_whenDataIsAvailable() = runTest {
        shoppingRepository.addShoppingItem(shoppingItem)

        val result = shoppingRepository.getAllShoppingItems().first()

        assertThat(result, `is`(Result.Success(listOf(shoppingItem))))
    }

    @Test
    fun getShoppingItemsByInBasketStatus_emitsSuccess_whenDataIsAvailable() = runTest {
        shoppingRepository.addShoppingItem(shoppingItem)

        val result = shoppingRepository.getShoppingItemsByInBasketStatus(inBasket = false).first()

        assertThat(result, `is`(Result.Success(listOf(shoppingItem))))
    }

    @Test
    fun getShoppingItemById_retrievesCorrectShoppingItem() = runTest {
        shoppingRepository.addShoppingItem(shoppingItem)

        val result = shoppingRepository.getShoppingItemById(itemId)

        assertThat(result, `is`(shoppingItem))
    }

    @Test
    fun addShoppingItem_addsShoppingItemToDatabase() = runTest {
        shoppingRepository.addShoppingItem(shoppingItem)

        val result = shoppingRepository.getShoppingItemById(itemId)

        assertThat(result, `is`(shoppingItem))
    }

    @Test
    fun updateShoppingItem_updatesShoppingItemInDatabase() = runTest {
        val updatedItem = shoppingItem.copy(itemName = "Updated Item")

        shoppingRepository.addShoppingItem(shoppingItem)
        shoppingRepository.updateShoppingItem(updatedItem)

        val result = shoppingRepository.getShoppingItemById(itemId)

        assertThat(result, `is`(updatedItem))
    }

    @Test
    fun deleteShoppingItem_deletesShoppingItemFromDatabase() = runTest {
        shoppingRepository.addShoppingItem(shoppingItem)

        shoppingRepository.deleteShoppingItem(shoppingItem)
        val result = shoppingRepository.getShoppingItemById(itemId)

        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun deleteShoppingItemsByInBasketStatus_deletesOnlyItemsWithMatchingStatus() = runTest {
        val itemToDelete = ShoppingItem(UUID.randomUUID(), "Item to Delete", inBasket = false)
        val itemToKeep = ShoppingItem(UUID.randomUUID(), "Item to Keep", inBasket = true)

        shoppingRepository.addShoppingItem(itemToDelete)
        shoppingRepository.addShoppingItem(itemToKeep)

        shoppingRepository.deleteShoppingItemsByInBasketStatus(inBasket = false)

        val result = shoppingRepository.getAllShoppingItems().first()

        assertThat(result, `is`(Result.Success(listOf(itemToKeep))))
        assertThat((result as Result.Success).data.contains(itemToDelete), `is`(false))
    }

    @Test
    fun deleteAllShoppingItems_deletesAllShoppingItems() = runTest {
        val item1 = ShoppingItem(UUID.randomUUID(), "Test Item", inBasket = false)
        val item2 = ShoppingItem(UUID.randomUUID(), "Test Item 2", inBasket = true)

        shoppingRepository.addShoppingItem(item1)
        shoppingRepository.addShoppingItem(item2)

        val beforeDelete = shoppingRepository.getAllShoppingItems().first()
        assertThat(beforeDelete, `is`(Result.Success(listOf(item1, item2))))

        shoppingRepository.deleteAllShoppingItems()

        val afterDelete = shoppingRepository.getAllShoppingItems().first()
        assertThat(afterDelete, `is`(Result.Success(emptyList())))
    }

    @Test
    fun deleteShoppingItem_doesNotThrowException_whenItemDoesNotExist() = runTest {
        assertThat(
            runCatching { shoppingRepository.deleteShoppingItem(shoppingItem) }.exceptionOrNull(),
            `is`(nullValue())
        )
    }
}
