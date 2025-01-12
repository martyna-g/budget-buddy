package pl.tinks.budgetbuddy.payment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.Result
import pl.tinks.budgetbuddy.payment.AddAndConfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.DeletePaymentAndCleanupUseCase
import pl.tinks.budgetbuddy.payment.GetPaymentByIdUseCase
import pl.tinks.budgetbuddy.payment.MoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.UndoMoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.UpdateAndReconfigurePaymentUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val getPaymentByIdUseCase: GetPaymentByIdUseCase,
    private val updateAndReconfigurePaymentUseCase: UpdateAndReconfigurePaymentUseCase,
    private val addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase,
    private val deletePaymentAndCleanupUseCase: DeletePaymentAndCleanupUseCase,
    private val moveToHistoryUseCase: MoveToHistoryUseCase,
    private val undoMoveToHistoryUseCase: UndoMoveToHistoryUseCase,
) : ViewModel() {

    private var _uiState: StateFlow<PaymentUiState> =
        paymentRepository.getAllPayments().map { result ->
            when (result) {
                is Result.Success -> PaymentUiState.Success(
                    result.data.filter {
                        !it.paymentCompleted
                    }.sortedBy { it.date })

                is Result.Error -> PaymentUiState.Error(result.e)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, PaymentUiState.Loading)

    val uiState: Flow<PaymentUiState> = _uiState

    private var _selectedPayment: MutableSharedFlow<Payment> = MutableSharedFlow()
    val selectedPayment: SharedFlow<Payment> = _selectedPayment

    fun initPaymentDetails(id: UUID) {
        viewModelScope.launch {
            val payment = getPaymentByIdUseCase.getPaymentById(id)
            _selectedPayment.emit(payment)
        }
    }

    fun addPayment(payment: Payment) {
        viewModelScope.launch {
            addAndConfigurePaymentUseCase(payment)
        }
    }

    fun updatePayment(payment: Payment) {
        viewModelScope.launch {
            updateAndReconfigurePaymentUseCase(payment)
        }
    }

    fun moveToHistory(payment: Payment) {
        viewModelScope.launch {
            moveToHistoryUseCase.moveToHistory(payment)
        }
    }

    fun undoMoveToHistory(payment: Payment) {
        viewModelScope.launch {
            undoMoveToHistoryUseCase.undoMoveToHistory(payment)
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            deletePaymentAndCleanupUseCase(payment)
        }
    }

}

sealed class PaymentUiState {
    data object Loading : PaymentUiState()
    data class Success(val data: List<Payment>) : PaymentUiState()
    data class Error(val e: Throwable) : PaymentUiState()
}
