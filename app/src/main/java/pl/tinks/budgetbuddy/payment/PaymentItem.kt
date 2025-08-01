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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onUndoCompleteClick: () -> Unit,
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
                    PaymentCompletedActionsRow(onUndoCompleteClick, onDeleteClick)
                } else if (payment.date.toLocalDate() > LocalDate.now()) {
                    PaymentUpcomingActionsRow(onEditClick, onDeleteClick)
                } else {
                    PaymentDueActionsRow(onCompleteClick, onCompleteClick)
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
    onEditClick: () -> Unit, onDeleteClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = onEditClick, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.edit))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onDeleteClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.delete))
        }
    }
}

@Composable
fun PaymentDueActionsRow(
    onMoveToHistoryClick: () -> Unit, onDeleteClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = onMoveToHistoryClick, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.complete))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onDeleteClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.delete))
        }
    }
}

@Composable
fun PaymentCompletedActionsRow(
    onUndoMoveToHistoryClick: () -> Unit, onDeleteClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onUndoMoveToHistoryClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Restore, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.restore))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onDeleteClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.delete))
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Preview(apiLevel = 33, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PaymentItemPreview() {
    PaymentItem(previewPayment,
        isExpanded = true,
        onExpandClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onCompleteClick = {},
        onUndoCompleteClick = {},
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
