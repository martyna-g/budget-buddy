package pl.tinks.budgetbuddy.payment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.Result
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel @Inject constructor(private val paymentRepository: PaymentRepository) :
    ViewModel() {

    private var _uiState: StateFlow<PaymentUiState> =
        paymentRepository.getAllPayments().map { result ->
            when (result) {
                is Result.Success -> PaymentUiState.Success(result.data)
                is Result.Error -> PaymentUiState.Error(result.e)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, PaymentUiState.Loading)

    val uiState: Flow<PaymentUiState> = _uiState

}

sealed class PaymentUiState {
    data object Loading : PaymentUiState()
    data class Success(val data: List<Payment>) : PaymentUiState()
    data class Error(val e: Throwable) : PaymentUiState()
}
