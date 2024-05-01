package pl.tinks.budgetbuddy.shopping

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "in_basket") var inBasket: Boolean = false,
)
