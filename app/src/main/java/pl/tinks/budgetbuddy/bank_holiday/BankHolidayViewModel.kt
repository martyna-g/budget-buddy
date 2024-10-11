package pl.tinks.budgetbuddy.bank_holiday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import pl.tinks.budgetbuddy.Result

@HiltViewModel
class BankHolidayViewModel @Inject constructor(
    private val bankHolidayRetriever: BankHolidayRetriever
) : ViewModel() {

    private val _uiState = MutableStateFlow<BankHolidayUiState>(BankHolidayUiState.Loading)
    val uiState: StateFlow<BankHolidayUiState> = _uiState

    init {
        getBankHolidays()
    }

    private fun getBankHolidays() {
        viewModelScope.launch {
            when (val result = bankHolidayRetriever.getBankHolidays()) {
                is Result.Success -> {
                    val today = LocalDate.now()
                    val filteredBankHolidays = result.data.filter { bankHoliday ->
                        bankHoliday.date.isEqual(today) || bankHoliday.date.isAfter(today)
                    }
                    _uiState.value = BankHolidayUiState.Success(filteredBankHolidays)
                }

                is Result.Error -> {
                    _uiState.value =
                        BankHolidayUiState.Error(result.e.message ?: "An unknown error occurred")
                }
            }
        }
    }

}

sealed class BankHolidayUiState {
    data class Success(val bankHolidays: List<BankHoliday>) : BankHolidayUiState()
    data class Error(val message: String) : BankHolidayUiState()
    data object Loading : BankHolidayUiState()
}
