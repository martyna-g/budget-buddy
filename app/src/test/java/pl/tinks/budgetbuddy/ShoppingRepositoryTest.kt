package pl.tinks.budgetbuddy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import pl.tinks.budgetbuddy.shopping.ShoppingRepositoryImpl
import java.util.UUID

class ShoppingRepositoryTest {

    private lateinit var shoppingDao: ShoppingDao
    private lateinit var shoppingRepository: ShoppingRepository
    private lateinit var itemId: UUID

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        shoppingDao = Mockito.mock(ShoppingDao::class.java)
        shoppingRepository = ShoppingRepositoryImpl(shoppingDao, UnconfinedTestDispatcher())
        itemId = UUID.randomUUID()
    }

    @Test
    fun `getAllShoppingItems emits success when dao returns data`() = runTest {
        val shoppingItems = listOf(ShoppingItem(itemId, "Test Item"))
        Mockito.`when`(shoppingDao.getAllShoppingItems()).thenReturn(flowOf(shoppingItems))

        val result = shoppingRepository.getAllShoppingItems().first()

        verify(shoppingDao).getAllShoppingItems()
        assertThat(result, `is`(Result.Success(shoppingItems)))
    }

    @Test
    fun `getAllShoppingItems emits success with empty list when dao returns no data`() =
        runTest {
            Mockito.`when`(shoppingDao.getAllShoppingItems()).thenReturn(flowOf(emptyList()))

            val result = shoppingRepository.getAllShoppingItems().first()

            verify(shoppingDao).getAllShoppingItems()
            assertThat(result, `is`(Result.Success(emptyList())))
        }

    @Test
    fun `getShoppingItemsByInBasketStatus emits success when dao returns data`() = runTest {
        val shoppingItems = listOf(ShoppingItem(itemId, "Test Item", inBasket = true))
        Mockito.`when`(shoppingDao.getShoppingItemsByInBasketStatus(true))
            .thenReturn(flowOf(shoppingItems))

        val result = shoppingRepository.getShoppingItemsByInBasketStatus(true).first()

        verify(shoppingDao).getShoppingItemsByInBasketStatus(true)
        assertThat(result, `is`(Result.Success(shoppingItems)))
    }

    @Test
    fun `getShoppingItemById returns correct item by id`() = runTest {
        val shoppingItem = ShoppingItem(itemId, "Test Item")
        Mockito.`when`(shoppingDao.getShoppingItemById(itemId)).thenReturn(shoppingItem)

        shoppingRepository.addShoppingItem(shoppingItem)

        val result = shoppingRepository.getShoppingItemById(itemId)

        verify(shoppingDao).getShoppingItemById(itemId)
        assertThat(result, `is`(shoppingItem))
    }

    @Test
    fun `getShoppingItemById returns null when item not found`() = runTest {
        val randomId = UUID.randomUUID()

        val result = shoppingRepository.getShoppingItemById(randomId)

        verify(shoppingDao).getShoppingItemById(randomId)
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun `addShoppingItem calls addShoppingItem on dao`() = runTest {
        val shoppingItem = ShoppingItem(itemId, "Test Item")

        shoppingRepository.addShoppingItem(shoppingItem)

        verify(shoppingDao).addShoppingItem(shoppingItem)
    }

    @Test
    fun `updateShoppingItem calls updateShoppingItem on dao`() = runTest {
        val updatedShoppingItem = ShoppingItem(itemId, "Updated Shopping Item")

        shoppingRepository.updateShoppingItem(updatedShoppingItem)

        verify(shoppingDao).updateShoppingItem(updatedShoppingItem)
    }



    @Test
    fun `deleteShoppingItem calls deleteShoppingItem on dao`() = runTest {
        val shoppingItem = ShoppingItem(itemId, "Test Item")
        shoppingRepository.deleteShoppingItem(shoppingItem)

        verify(shoppingDao).deleteShoppingItem(shoppingItem)
    }

    @Test
    fun `deleteShoppingItemsByInBasketStatus calls deleteShoppingItemsByInBasketStatus on dao`() =
        runTest {
            shoppingRepository.deleteShoppingItemsByInBasketStatus(inBasket = true)

            verify(shoppingDao).deleteShoppingItemsByInBasketStatus(true)
        }

    @Test
    fun `deleteAllShoppingItems calls deleteAllShoppingItems on dao`() = runTest {
        shoppingRepository.deleteAllShoppingItems()

        verify(shoppingDao).deleteAllShoppingItems()
    }
}
