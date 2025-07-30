package pl.tinks.budgetbuddy.shopping

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun ShoppingListItem(
    shoppingItem: ShoppingItem,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Card(
        shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = background
        ), onClick = onClick, modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = if (shoppingItem.inBasket) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (shoppingItem.inBasket) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = shoppingItem.itemName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun ShoppingListItemPreview() {
    ShoppingListItem(
        ShoppingItem(UUID.randomUUID(), "Preview Item", inBasket = true),
        isSelected = false,
        onClick = { },
    )
}
