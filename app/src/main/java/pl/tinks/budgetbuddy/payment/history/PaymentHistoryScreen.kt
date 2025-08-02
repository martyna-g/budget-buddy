package pl.tinks.budgetbuddy.payment.history

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.ui.theme.BudgetBuddyTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Composable
fun PaymentHistoryScreen(
    viewModel: PaymentHistoryViewModel,
    onBackClick: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState(initial = PaymentHistoryUiState.Loading)

    PaymentHistoryScreenContent(
        state = state,
        onBackClick = onBackClick,
        onErrorDialogDismiss = onErrorDialogDismiss,
        onDeleteClick = viewModel::deletePayment,
        onUndoCompleteClick = viewModel::undoMoveToHistory,
        modifier = modifier
    )
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentHistoryScreenPreview() {
    BudgetBuddyTheme {
        PaymentHistoryScreenContent(PaymentHistoryUiState.Success(previewHistoryList),
            {},
            {},
            {},
            {})
    }
}

val previewHistoryPayment = Payment(
    UUID.randomUUID(),
    "Preview Payment",
    Money.of(CurrencyUnit.GBP, 42.50),
    LocalDateTime.of(LocalDate.of(2024, 7, 23), LocalTime.now()),
    PaymentFrequency.DAILY,
    paymentCompleted = true
)

val previewHistoryList = listOf(
    PaymentListItem.DynamicHeader("August 2025"),
    PaymentListItem.PaymentEntry(
        previewHistoryPayment.copy(
            date = LocalDateTime.of(LocalDate.of(2025, 8, 1), LocalTime.now())
        )
    ),
    PaymentListItem.DynamicHeader("July 2024"),
    PaymentListItem.PaymentEntry(previewHistoryPayment.copy(id = UUID.randomUUID())),
    PaymentListItem.PaymentEntry(previewHistoryPayment.copy(id = UUID.randomUUID()))
)
