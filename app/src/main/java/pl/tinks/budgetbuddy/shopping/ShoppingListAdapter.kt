package pl.tinks.budgetbuddy.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemShoppingBinding

class ShoppingListAdapter(
    private val shoppingItemClickCallback: (ShoppingItem) -> Unit,
    private val shoppingItemLongClickCallback: () -> Unit,
    private val emptyListCallback: () -> Unit
) : ListAdapter<ShoppingItem, ShoppingListAdapter.ShoppingListViewHolder>(ShoppingDiffCallback()) {

     val selectedItems = mutableListOf<ShoppingItem>()

    inner class ShoppingListViewHolder(
        private val binding: ItemShoppingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shoppingItem: ShoppingItem) {

            binding.textShoppingItemName.text = shoppingItem.itemName
            itemView.background = null
            if (shoppingItem.isCollected) {
                binding.imageShoppingItemChecked.visibility = View.VISIBLE
            } else {
                binding.imageShoppingItemChecked.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if (selectedItems.isEmpty()) {
                    shoppingItemClickCallback(shoppingItem)
                } else {

                    if (selectedItems.contains(shoppingItem)) {
                        selectedItems.remove(shoppingItem)
                        itemView.background = null
                        if (selectedItems.isEmpty()) emptyListCallback()
                    } else {
                        selectedItems.add(shoppingItem)
                        itemView.setBackgroundResource(R.color.light_grey)
                    }
                }
            }

            itemView.setOnLongClickListener {
                if (selectedItems.isEmpty()) {
                    selectedItems.add(shoppingItem)
                    itemView.setBackgroundResource(R.color.light_grey)
                    shoppingItemLongClickCallback()
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ItemShoppingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ShoppingDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem == newItem
        }
    }
}
