package pl.tinks.budgetbuddy.payment.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreenContent(
    state: PaymentHistoryUiState,
    onBackClick: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    onDeleteClick: (Payment) -> Unit,
    onUndoCompleteClick: (Payment) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(R.string.payment_history),
                color = MaterialTheme.colorScheme.primary
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        })
    }) { innerPadding ->
        when (state) {
            is PaymentHistoryUiState.Success -> {
                PaymentListContent(
                    paymentListItems = state.data,
                    onDeleteClick = onDeleteClick,
                    onUndoCompleteClick = onUndoCompleteClick,
                    modifier = modifier.padding(innerPadding)
                )
            }

            is PaymentHistoryUiState.Error -> {
                ErrorScreen(
                    onOk = onErrorDialogDismiss, modifier = modifier.padding(innerPadding)
                )
            }

            PaymentHistoryUiState.Loading -> {
                LoadingScreen(modifier = modifier.padding(innerPadding))
            }
        }
    }
}
