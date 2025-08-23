package pl.tinks.budgetbuddy.payment.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListContent
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.ui.theme.BudgetBuddyTheme
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentListScreenContent(
    state: PaymentUiState,
    onAddClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    onDeleteClick: (Payment) -> Unit,
    onEditClick: (Payment) -> Unit = {},
    onCompleteClick: (Payment) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
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
                PaymentListContent(
                    paymentListItems = state.data,
                    onDeleteClick = onDeleteClick,
                    onEditClick = onEditClick,
                    onCompleteClick = onCompleteClick,
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

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentListScreenPreview() {
    BudgetBuddyTheme {
        PaymentListScreenContent(PaymentUiState.Success(previewList), {}, {}, {}, {})
    }
}

val previewListScreenPayment = Payment(
    UUID.randomUUID(),
    "Preview Payment",
    Money.of(CurrencyUnit.GBP, 42.50),
    LocalDateTime.now().plusDays(14).truncatedTo(ChronoUnit.SECONDS),
    PaymentFrequency.DAILY,
)

val previewList = listOf(
    PaymentListItem.StaticHeader(R.string.overdue_payments),
    PaymentListItem.PaymentEntry(previewListScreenPayment),
    PaymentListItem.StaticHeader(R.string.payments_due_today),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Today's Payment", id = UUID.randomUUID()
        )
    ),
    PaymentListItem.StaticHeader(R.string.upcoming_payments),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Upcoming Payment", id = UUID.randomUUID()
        )
    )
)
