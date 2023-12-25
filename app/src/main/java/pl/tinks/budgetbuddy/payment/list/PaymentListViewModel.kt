package pl.tinks.budgetbuddy.payment.list

import androidx.lifecycle.ViewModel
import pl.tinks.budgetbuddy.payment.PaymentRepository

class PaymentListViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {
}

sealed class PaymentUiState<out T> {
    data object Loading : PaymentUiState<Nothing>()
    data class Success<T>(val data: T) : PaymentUiState<T>()
    data class Error(val e: Throwable) : PaymentUiState<Nothing>()
}
