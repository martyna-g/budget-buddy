package pl.tinks.budgetbuddy.payment.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.PaymentListScreenContent
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentListScreen(
    viewModel: PaymentListViewModel,
    onAddClick: () -> Unit,
    onEditClick: (UUID) -> Unit,
    onHistoryClick: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState(PaymentUiState.Loading)

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }, actions = {
            IconButton(onClick = onHistoryClick) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.open_payment_history)
                )
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_payment)
            )
        }
    }) { innerPadding ->
        when (state) {
            is PaymentUiState.Success -> {
                PaymentListScreenContent(
                    paymentListItems = (state as PaymentUiState.Success).data,
                    onDeleteClick = viewModel::deletePayment,
                    onEditClick = { payment -> onEditClick(payment.id) },
                    onCompleteClick = viewModel::moveToHistory,
                    modifier = modifier.padding(innerPadding)
                )
            }

            is PaymentUiState.Error -> {
                ErrorScreen(onErrorDialogDismiss, modifier = modifier.padding(innerPadding))
            }

            PaymentUiState.Loading -> {
                LoadingScreen(modifier = modifier.padding(innerPadding))
            }
        }
    }
}
