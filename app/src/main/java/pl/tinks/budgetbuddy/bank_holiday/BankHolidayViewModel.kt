package pl.tinks.budgetbuddy.bank_holiday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BankHolidayViewModel @Inject constructor(
    private val bankHolidayRetriever: BankHolidayRetriever
) : ViewModel() {

    private var _uiState: StateFlow<BankHolidayUiState> = flow {
        val bankHolidays: List<BankHoliday> = bankHolidayRetriever.getBankHolidays()
        emit(bankHolidays)
    }.map { bankHolidays ->
        val today = LocalDate.now()
        val filteredBankHolidays = bankHolidays.filter { bankHoliday ->
            bankHoliday.date.isEqual(today) || bankHoliday.date.isAfter(today)
        }
        BankHolidayUiState.Success(filteredBankHolidays)
    }.catch { e ->
        BankHolidayUiState.Error(e.message ?: "An unknown error occurred")
    }.stateIn(viewModelScope, SharingStarted.Lazily, BankHolidayUiState.Loading)

    val uiState: Flow<BankHolidayUiState> = _uiState

}

sealed class BankHolidayUiState {
    data class Success(val bankHolidays: List<BankHoliday>) : BankHolidayUiState()
    data class Error(val message: String) : BankHolidayUiState()
    data object Loading : BankHolidayUiState()
}
