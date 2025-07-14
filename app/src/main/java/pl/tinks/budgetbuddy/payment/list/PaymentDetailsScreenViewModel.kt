package pl.tinks.budgetbuddy.payment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.payment.Payment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsScreenViewModel @Inject constructor(
    private val addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase,
    private val updateAndReconfigurePaymentUseCase: UpdateAndReconfigurePaymentUseCase,
    private val getPaymentByIdUseCase: GetPaymentByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentDetailsUiState())
    val uiState: StateFlow<PaymentDetailsUiState> = _uiState

    fun loadPayment(id: UUID) {
        viewModelScope.launch {
            val payment = getPaymentByIdUseCase(id)
            _uiState.update { currentState ->
                currentState.copy(
                    id = payment.id,
                    title = payment.title,
                    amount = payment.amount.amount.toPlainString(),
                    date = payment.date.toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    frequency = payment.frequency,
                    notificationEnabled = payment.notificationEnabled,
                    isEdit = true
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun onDateChange(value: String) {
        _uiState.update { it.copy(date = value) }
    }

    fun onFrequencyChange(value: PaymentFrequency) {
        _uiState.update { it.copy(frequency = value) }
    }

    fun onNotificationChange(value: Boolean) {
        _uiState.update { it.copy(notificationEnabled = value) }
    }

    fun onSavePayment() {
        val state = _uiState.value
        if (!validateFields(state)) return

        val date = parseDateSafely(state.date) ?: return
        val amount = state.amount.toDoubleOrNull() ?: 0.00

        val payment = Payment(
            id = state.id ?: UUID.randomUUID(),
            title = state.title,
            amount = Money.of(CurrencyUnit.GBP, amount),
            date = date.atStartOfDay(),
            frequency = state.frequency!!,
            notificationEnabled = state.notificationEnabled,
        )
        viewModelScope.launch {
            if (state.isEdit) updatePayment(payment) else addPayment(payment)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    private suspend fun addPayment(payment: Payment) {
        addAndConfigurePaymentUseCase(payment)
    }

    private suspend fun updatePayment(payment: Payment) {
        updateAndReconfigurePaymentUseCase(payment)
    }

    private fun validateFields(state: PaymentDetailsUiState): Boolean {
        var isValid = true
        if (state.title.isBlank()) isValid = false
        if (state.amount.isBlank() || state.amount.toDoubleOrNull() == null) isValid = false
        if (state.date.isBlank() || parseDateSafely(state.date) == null) isValid = false
        if (state.frequency == null) isValid = false
        return isValid
    }

    private fun parseDateSafely(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            null
        }
    }

}

data class PaymentDetailsUiState(
    val id: UUID? = null,
    val title: String = "",
    val amount: String = "",
    val date: String = "",
    val frequency: PaymentFrequency? = null,
    val notificationEnabled: Boolean = false,
    val isSaved: Boolean = false,
    val isEdit: Boolean = false,
)
