package pl.tinks.budgetbuddy.bankholiday

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen

@Composable
fun BankHolidayScreen(
    viewModel: BankHolidayViewModel,
    region: String,
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    when (state) {
        is BankHolidayUiState.Success -> {
            val bankHolidays =
                (state as BankHolidayUiState.Success).bankHolidays.filter { it.region == region }
            val nextBankHoliday = bankHolidays.firstOrNull()

            Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                NextBankHolidaySection(nextBankHoliday)
                Spacer(Modifier.height(8.dp))
                BankHolidayScreenContent(bankHolidays)
            }
        }

        is BankHolidayUiState.Loading -> LoadingScreen()
        is BankHolidayUiState.Error -> ErrorScreen(onOk = onErrorDialogDismiss)
    }
}
