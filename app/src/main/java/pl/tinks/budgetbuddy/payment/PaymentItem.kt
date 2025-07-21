package pl.tinks.budgetbuddy.payment

import android.content.res.Configuration
import android.icu.text.NumberFormat
import android.icu.util.Currency
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID

@Composable
fun PaymentItem(
    payment: Payment,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveToHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = { onExpandClick() }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PaymentDate(payment.date)
                Spacer(Modifier.size(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    PaymentTitle(payment.title)
                    PaymentAmount(payment.amount)
                }
            }
            if (isExpanded) {
                if (payment.paymentCompleted) {
                    PaymentCompletedActionsRow(onInfoClick, onDeleteClick)
                } else if (payment.date.toLocalDate() > LocalDate.now()) {
                    PaymentUpcomingActionsRow(onInfoClick, onEditClick, onDeleteClick)
                } else {
                    PaymentDueActionsRow(onInfoClick, onMoveToHistoryClick)
                }
            }
        }
    }
}

@Composable
fun PaymentDate(date: LocalDateTime, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .defaultMinSize(minWidth = 60.dp)
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = "${date.dayOfMonth}", style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PaymentTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title, style = MaterialTheme.typography.titleMedium, modifier = modifier
    )
}

@Composable
fun PaymentAmount(amount: Money, modifier: Modifier = Modifier) {
    Text(
        text = amount.displayString(),
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Composable
fun PaymentUpcomingActionsRow(
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, contentDescription = stringResource(R.string.action_info))
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_edit))
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
        }
    }
}

@Composable
fun PaymentDueActionsRow(
    onInfoClick: () -> Unit, onMoveToHistoryClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, contentDescription = stringResource(R.string.action_info))
        }
        IconButton(onClick = onMoveToHistoryClick) {
            Icon(
                Icons.Default.Check,
                contentDescription = stringResource(R.string.action_move_to_history)
            )
        }
    }
}

@Composable
fun PaymentCompletedActionsRow(
    onInfoClick: () -> Unit, onDeleteClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, contentDescription = stringResource(R.string.action_info))
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentItemPreview() {
    PaymentItem(
        previewPayment,
        isExpanded = false,
        onExpandClick = {},
        onInfoClick = {},
        onEditClick = {},
        onDeleteClick = { },
        onMoveToHistoryClick = { },
        modifier = Modifier
    )
}

val previewPayment = Payment(
    UUID.randomUUID(),
    "Some Payment",
    Money.of(CurrencyUnit.GBP, 42.50),
    LocalDateTime.now().plusDays(14).truncatedTo(ChronoUnit.SECONDS),
    PaymentFrequency.DAILY,
)

fun Money.displayString(locale: Locale = Locale.getDefault()): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.currency = Currency.getInstance(currencyUnit.code)
    return format.format(amount)
}
