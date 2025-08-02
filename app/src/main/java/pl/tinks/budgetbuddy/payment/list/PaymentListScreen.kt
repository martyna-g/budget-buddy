package pl.tinks.budgetbuddy.payment.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import java.util.UUID

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

    PaymentListScreenContent(
        state = state,
        onAddClick = onAddClick,
        onHistoryClick = onHistoryClick,
        onErrorDialogDismiss = onErrorDialogDismiss,
        onDeleteClick = viewModel::deletePayment,
        onEditClick = { payment -> onEditClick(payment.id) },
        onCompleteClick = viewModel::moveToHistory,
        modifier = modifier
    )
}
