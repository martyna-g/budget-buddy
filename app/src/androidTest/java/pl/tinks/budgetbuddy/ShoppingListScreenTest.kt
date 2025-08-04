package pl.tinks.budgetbuddy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingListContent
import pl.tinks.budgetbuddy.shopping.ShoppingListScreenContent
import pl.tinks.budgetbuddy.shopping.ShoppingListUiState
import java.util.UUID

class ShoppingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shoppingListScreenContent_displaysAllItems() {
        val shoppingItem = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 1")
        val shoppingItem2 = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 2")
        val shoppingItem3 = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 3")
        val items = listOf(shoppingItem, shoppingItem2, shoppingItem3)

        composeTestRule.setContent {
            ShoppingListContent(items = items, onItemClick = {})
        }

        composeTestRule.onNodeWithText("Test Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Item 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Item 3").assertIsDisplayed()
    }

    @Test
    fun addItemButton_callsOnAddClick() {
        var onAddClickCalled = false

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf()),
                onAddClick = { onAddClickCalled = true },
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithText("Add item").performTextInput("Test")
        composeTestRule.onNodeWithContentDescription("Add item").performClick()
        assertTrue(onAddClickCalled)
    }

    @Test
    fun shoppingItem_click_callsOnItemClick() {
        var onItemClickCalled = false
        val shoppingItem = ShoppingItem(UUID.randomUUID(), "Test Item")

        composeTestRule.setContent {
            ShoppingListContent(items = listOf(shoppingItem),
                onItemClick = { onItemClickCalled = true })
        }

        composeTestRule.onNodeWithText("Test Item").performClick()
        assertTrue(onItemClickCalled)
    }

    @Test
    fun selectMultipleItems_entersSelectionModeAndShowsDeleteIcon() {
        val shoppingItem = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 1")

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf(shoppingItem)),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Select multiple").performClick()
        composeTestRule.onNodeWithText("Test Item 1").performClick()
        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete selected").assertIsDisplayed()
    }

    @Test
    fun deselectAllItems_exitsSelectionModeAndHidesDeleteIcon() {
        val shoppingItem = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 1")

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf(shoppingItem)),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Select multiple").performClick()
        composeTestRule.onNodeWithText("Test Item 1").performClick()
        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete selected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Item 1").performClick()
        composeTestRule.onNodeWithText("1 selected").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Delete selected").assertIsNotDisplayed()
    }

    @Test
    fun selectMultiple_countsSelectedItems() {
        val shoppingItem = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 1")
        val shoppingItem2 = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 2")
        val shoppingItem3 = ShoppingItem(id = UUID.randomUUID(), itemName = "Test Item 3")
        val items = listOf(shoppingItem, shoppingItem2, shoppingItem3)

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(items),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Select multiple").performClick()
        composeTestRule.onNodeWithText("Test Item 1").performClick()
        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Item 2").performClick()
        composeTestRule.onNodeWithText("2 selected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Item 3").performClick()
        composeTestRule.onNodeWithText("3 selected").assertIsDisplayed()
    }

    @Test
    fun deleteSelectedItems_callsOnDeleteItems() {
        var onDeleteItemsCalled = false

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf()),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = { onDeleteItemsCalled = true },
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Select multiple").performClick()
        composeTestRule.onNodeWithContentDescription("Delete selected").performClick()
        assertTrue(onDeleteItemsCalled)
    }

    @Test
    fun deleteAllItemsMenu_callsDeleteAll() {
        var onDeleteAllCalled = false

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf()),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = { onDeleteAllCalled = true },
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete all").performClick()
        assertTrue(onDeleteAllCalled)
    }

    @Test
    fun deleteCheckedItemsMenu_callsDeleteChecked() {
        var onDeleteCheckedCalled = false

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf()),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = { onDeleteCheckedCalled = true },
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete checked").performClick()
        assertTrue(onDeleteCheckedCalled)
    }

    @Test
    fun deleteUncheckedItemsMenu_callsOnDeleteUnchecked() {
        var onDeleteUncheckedCalled = false

        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Success(listOf()),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = { onDeleteUncheckedCalled = true },
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete unchecked").performClick()
        assertTrue(onDeleteUncheckedCalled)
    }

    @Test
    fun shoppingListScreenContent_error_showsErrorScreen() {
        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Error(Throwable("error")),
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithText(
            "There was an error loading data.\n" + "Please try again later."
        ).assertIsDisplayed()
    }

    @Test
    fun shoppingListScreenContent_loading_showsLoadingScreen() {
        composeTestRule.setContent {
            ShoppingListScreenContent(state = ShoppingListUiState.Loading,
                onAddClick = {},
                onUpdateItem = {},
                onDeleteItems = {},
                onDeleteAll = {},
                onDeleteChecked = {},
                onDeleteUnchecked = {},
                onErrorDialogDismiss = {})
        }

        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }

    @Test
    fun shoppingListContent_emptyList_showsNoItems() {
        composeTestRule.setContent {
            ShoppingListContent(items = listOf(), onItemClick = {})
        }
    }
}
