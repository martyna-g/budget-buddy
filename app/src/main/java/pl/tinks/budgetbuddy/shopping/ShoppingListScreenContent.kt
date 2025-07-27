package pl.tinks.budgetbuddy.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun ShoppingListScreenContent(
    items: List<ShoppingItem>,
    selectedItems: Set<UUID>,
    onItemClick: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ShoppingListItem(
                shoppingItem = item,
                isSelected = selectedItems.contains(item.id),
                onClick = { onItemClick(item) },
            )
        }
    }
}
