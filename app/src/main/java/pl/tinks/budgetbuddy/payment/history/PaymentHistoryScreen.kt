package pl.tinks.budgetbuddy.payment.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.PaymentListScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    viewModel: PaymentHistoryViewModel,
    onBackClick: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState(initial = PaymentHistoryUiState.Loading)

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.payment_history)) }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
    }) { innerPadding ->
        when (state) {
            is PaymentHistoryUiState.Success -> {
                PaymentListScreenContent(
                    paymentListItems = (state as PaymentHistoryUiState.Success).data,
                    onDeleteClick = viewModel::deletePayment,
                    onUndoCompleteClick = viewModel::undoMoveToHistory,
                    modifier = modifier.padding(innerPadding),
                )
            }

            is PaymentHistoryUiState.Error -> {
                ErrorScreen(
                    onOk = onErrorDialogDismiss,
                    modifier = modifier.padding(innerPadding)
                )
            }

            PaymentHistoryUiState.Loading -> {
                LoadingScreen(modifier = modifier.padding(innerPadding))
            }
        }
    }
}
