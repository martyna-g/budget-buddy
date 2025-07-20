package pl.tinks.budgetbuddy.payment

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
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.SectionHeader
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun PaymentListScreenContent(
    paymentListItems: List<PaymentListItem>,
    onInfoClick: (Payment) -> Unit,
    onDeleteClick: (Payment) -> Unit,
    onEditClick: (Payment) -> Unit = { },
    onMoveToHistoryClick: (Payment) -> Unit = { },
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
                is PaymentListItem.StaticHeader -> "static_header_${item.resId}"
                is PaymentListItem.DynamicHeader -> "dynamic_header_${item.headerText}"
                is PaymentListItem.PaymentEntry -> item.payment.id
            }
        }) { item ->
            when (item) {
                is PaymentListItem.StaticHeader -> SectionHeader(stringResource(item.resId))
                is PaymentListItem.DynamicHeader -> SectionHeader(item.headerText)
                is PaymentListItem.PaymentEntry -> PaymentItem(
                    payment = item.payment,
                    onInfoClick = { onInfoClick(item.payment) },
                    onEditClick = { onEditClick(item.payment) },
                    onDeleteClick = { onDeleteClick(item.payment) },
                    onMoveToHistoryClick = { onMoveToHistoryClick(item.payment) },
                )
            }
        }
    }
}


@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentListScreenPreview() {
    PaymentListScreenContent(previewList, {}, {}, {}, {})
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentListHistoryScreenPreview() {
    PaymentListScreenContent(previewHistoryList, {}, {}, {}, {})
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
