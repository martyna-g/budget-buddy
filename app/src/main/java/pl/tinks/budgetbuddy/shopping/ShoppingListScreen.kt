package pl.tinks.budgetbuddy.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.UUID

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState(ShoppingListUiState.Loading)

    ShoppingListScreenContent(
        state = state,
        onAddClick = { itemName ->
            viewModel.addShoppingItem(ShoppingItem(UUID.randomUUID(), itemName))
        },
        onUpdateItem = { item -> viewModel.updateShoppingItem(item) },
        onDeleteItems = { selected -> viewModel.deleteItems(selected) },
        onDeleteAll = { viewModel.deleteAllShoppingItems() },
        onDeleteChecked = { viewModel.deleteCheckedItems() },
        onDeleteUnchecked = { viewModel.deleteUncheckedItems() },
        onErrorDialogDismiss = onErrorDialogDismiss,
        modifier = modifier
    )
}
