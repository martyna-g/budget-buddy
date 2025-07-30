package pl.tinks.budgetbuddy.bankholiday

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BankHolidayListItem(bankHoliday: BankHoliday, modifier: Modifier = Modifier) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM") }
    val dayOfWeekFormatter = remember { DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = bankHoliday.date.format(dateFormatter),
            modifier = Modifier.weight(0.2f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = bankHoliday.date.format(dayOfWeekFormatter),
            modifier = Modifier.weight(0.3f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = bankHoliday.title,
            modifier = Modifier.weight(0.5f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun BankHolidayListItemPreview() {
    BankHolidayListItem(BankHoliday("England", "Preview Text", LocalDate.now()))
}
