package pl.tinks.budgetbuddy.bankholiday

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NextBankHolidaySection(
    bankHoliday: BankHoliday?, modifier: Modifier = Modifier
) {
    if (bankHoliday == null) return
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = bankHoliday.date.format(dateFormatter),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = bankHoliday.title, style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun NextBankHolidaySectionPreview() {
    NextBankHolidaySection(
        BankHoliday(
            region = "England", title = "Preview Title", date = LocalDate.now()
        )
    )
}
