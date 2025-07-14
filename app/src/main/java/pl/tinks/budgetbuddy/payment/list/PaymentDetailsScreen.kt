package pl.tinks.budgetbuddy.payment.list

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PaymentDetailsScreen(
    viewModel: PaymentDetailsScreenViewModel,
    onCancel: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onCancel()
        }
    }

    PaymentDetailsScreenContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onAmountChange = viewModel::onAmountChange,
        onDateChange = viewModel::onDateChange,
        onFrequencyChange = viewModel::onFrequencyChange,
        onNotificationChange = viewModel::onNotificationChange,
        onSavePayment = viewModel::onSavePayment,
        onCancel = onCancel,
    )
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentDetailsScreenPreview() {
    PaymentDetailsScreenContent(
        state = previewState,
        onTitleChange = { },
        onAmountChange = { },
        onDateChange = { },
        onFrequencyChange = { },
        onNotificationChange = { },
        onSavePayment = { },
        onCancel = { },
    )
}

val previewState = PaymentDetailsUiState(
    title = "Netflix",
    amount = "42.00",
    date = "10/07/2025",
    frequency = PaymentFrequency.MONTHLY,
    notificationEnabled = true,
    isEdit = true
)
