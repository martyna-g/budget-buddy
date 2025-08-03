package pl.tinks.budgetbuddy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import pl.tinks.budgetbuddy.shopping.ShoppingListUiState
import pl.tinks.budgetbuddy.shopping.ShoppingListViewModel
import java.util.UUID

class ShoppingListViewModelTest {

    private lateinit var shoppingRepository: ShoppingRepository
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var item: ShoppingItem

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        shoppingRepository = Mockito.mock()
        viewModel = ShoppingListViewModel(shoppingRepository)
        item = ShoppingItem(UUID.randomUUID(), "Test Item")
    }

    @Test
    fun `uiState reflects Success when repository returns data`() = runTest {
        val shoppingList = listOf(item)

        Mockito.`when`(shoppingRepository.getAllShoppingItems())
            .thenReturn(flowOf(Result.Success(shoppingList)))

        val viewModel = ShoppingListViewModel(shoppingRepository)

        val result = viewModel.uiState.first()

        assertThat(result, `is`(ShoppingListUiState.Success(shoppingList)))
    }

    @Test
    fun `addShoppingItem calls addShoppingItem on repository`() = runTest {
        viewModel.addShoppingItem(item)

        verify(shoppingRepository).addShoppingItem(item)
    }

    @Test
    fun `updateShoppingItem calls updateShoppingItem on repository`() = runTest {
        viewModel.updateShoppingItem(item)

        verify(shoppingRepository).updateShoppingItem(item)
    }

    @Test
    fun `deleteItems calls deleteShoppingItems`() = runTest {
        val item2 = ShoppingItem(UUID.randomUUID(), "Test Item 2")
        val items = listOf(item, item2)
        val itemIds = setOf(item.id, item2.id)

        Mockito.`when`(shoppingRepository.getAllShoppingItems())
            .thenReturn(flowOf(Result.Success(items)))

        val viewModel = ShoppingListViewModel(shoppingRepository)

        viewModel.uiState.dropWhile { it is ShoppingListUiState.Loading }.first()

        viewModel.deleteItems(itemIds)

        verify(shoppingRepository).deleteShoppingItems(items)
    }

    @Test
    fun `deleteCheckedItems calls repository to delete items in basket`() = runTest {
        viewModel.deleteCheckedItems()

        verify(shoppingRepository).deleteShoppingItemsByInBasketStatus(true)
    }

    @Test
    fun `deleteUncheckedItems calls repository to delete items not in basket`() = runTest {
        viewModel.deleteUncheckedItems()

        verify(shoppingRepository).deleteShoppingItemsByInBasketStatus(false)
    }

    @Test
    fun `deleteAllShoppingItems calls deleteAllShoppingItems on repository`() = runTest {
        viewModel.deleteAllShoppingItems()

        verify(shoppingRepository).deleteAllShoppingItems()
    }
}
