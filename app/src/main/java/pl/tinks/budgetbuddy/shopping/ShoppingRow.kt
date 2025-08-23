package pl.tinks.budgetbuddy.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun ShoppingRow(
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
    val (icon, tint) = if (shoppingItem.inBasket) {
        Icons.Outlined.CheckCircle to MaterialTheme.colorScheme.primary
    } else {
        Icons.Outlined.Circle to MaterialTheme.colorScheme.onSurfaceVariant
    }

    ListItem(
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
        },
        headlineContent = {
            Text(
                text = shoppingItem.itemName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        colors = ListItemDefaults.colors(background),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    )
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun ShoppingRowPreview() {
    ShoppingRow(
        ShoppingItem(UUID.randomUUID(), "Preview Item", inBasket = true),
        isSelected = false,
        onClick = { },
    )
}
