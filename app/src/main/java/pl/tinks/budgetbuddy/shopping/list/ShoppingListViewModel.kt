package pl.tinks.budgetbuddy.shopping.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

}

sealed class ShoppingListUiState {
    data object Loading : ShoppingListUiState()
    data class Success(val data: List<ShoppingItem>) : ShoppingListUiState()
    data class Error(val e: Throwable) : ShoppingListUiState()
}
