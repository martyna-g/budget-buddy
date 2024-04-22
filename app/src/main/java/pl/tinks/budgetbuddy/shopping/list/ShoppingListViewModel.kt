package pl.tinks.budgetbuddy.shopping.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.Result
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingRepository
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private var _uiState = shoppingRepository.getAllShoppingItems().map { result ->
        when (result) {
            is Result.Success -> ShoppingListUiState.Success(result.data)
            is Result.Error -> ShoppingListUiState.Error(result.e)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, ShoppingListUiState.Loading)

    val uiState: Flow<ShoppingListUiState> = _uiState

    fun addShoppingItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.addShoppingItem(shoppingItem)
        }
    }

    fun updateShoppingItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.updateShoppingItem(shoppingItem)
        }
    }

    fun deleteSelectedItems(selectedItems: List<ShoppingItem>) {
        val items = ArrayList(selectedItems)
        viewModelScope.launch {
            items.forEach {
                shoppingRepository.deleteShoppingItem(it)
            }
        }
    }

}

sealed class ShoppingListUiState {
    data object Loading : ShoppingListUiState()
    data class Success(val data: List<ShoppingItem>) : ShoppingListUiState()
    data class Error(val e: Throwable) : ShoppingListUiState()
}
