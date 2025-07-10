package pl.tinks.budgetbuddy.payment.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.ItemHeader
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentItem
import pl.tinks.budgetbuddy.payment.PaymentListItem
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun PaymentListScreen(
    paymentListItems: List<PaymentListItem>,
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveToHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(paymentListItems, key = { item ->
            when (item) {
                is PaymentListItem.Header -> "header_${item.resId}"
                is PaymentListItem.PaymentEntry -> item.payment.id
            }
        }) { item ->
            when (item) {
                is PaymentListItem.Header -> ItemHeader(stringResource(item.resId))
                is PaymentListItem.PaymentEntry -> PaymentItem(
                    item.payment, onInfoClick, onEditClick, onDeleteClick, onMoveToHistoryClick
                )
            }
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentListScreenPreview() {
    PaymentListScreen(previewList, {}, {}, {}, {})
}

val previewListScreenPayment = Payment(
    UUID.randomUUID(),
    "Preview Payment",
    Money.of(CurrencyUnit.GBP, 42.50),
    LocalDateTime.now().plusDays(14).truncatedTo(ChronoUnit.SECONDS),
    PaymentFrequency.DAILY,
)

val previewList = listOf(
    PaymentListItem.Header(R.string.overdue_payments),
    PaymentListItem.PaymentEntry(previewListScreenPayment),
    PaymentListItem.Header(R.string.payments_due_today),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Today's Payment", id = UUID.randomUUID()
        )
    ),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Today's Payment 2", id = UUID.randomUUID()
        )
    ),
    PaymentListItem.Header(R.string.upcoming_payments),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Upcoming Payment", id = UUID.randomUUID()
        )
    ),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Upcoming Payment 2", id = UUID.randomUUID()
        )
    ),
    PaymentListItem.PaymentEntry(
        previewListScreenPayment.copy(
            title = "Upcoming Payment 3", id = UUID.randomUUID()
        )
    )
)
