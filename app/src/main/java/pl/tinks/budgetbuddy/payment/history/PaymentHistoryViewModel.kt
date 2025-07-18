package pl.tinks.budgetbuddy.payment.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.Result
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    private var _uiState = repository.getAllPayments().map { result ->
        when (result) {
            is Result.Success -> {
                val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
                val historyItems = result.data
                    .filter { it.paymentCompleted }
                    .sortedByDescending { it.date }
                    .groupBy { it.date.format(formatter) }
                    .flatMap { (month, paymentsInMonth) ->
                        listOf(PaymentListItem.DynamicHeader(month)) + paymentsInMonth.map {
                            PaymentListItem.PaymentEntry(it)
                        }
                    }
                PaymentHistoryUiState.Success(historyItems)
            }

            is Result.Error -> PaymentHistoryUiState.Error(result.e)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, PaymentHistoryUiState.Loading)

    val uiState: Flow<PaymentHistoryUiState> = _uiState

    fun deleteSelectedPayments(paymentList: List<Payment>) {
        viewModelScope.launch {
            repository.deletePayments(paymentList)
        }
    }
}

sealed class PaymentHistoryUiState {
    data object Loading : PaymentHistoryUiState()
    data class Success(val data: List<PaymentListItem>) : PaymentHistoryUiState()
    data class Error(val e: Throwable) : PaymentHistoryUiState()
}
