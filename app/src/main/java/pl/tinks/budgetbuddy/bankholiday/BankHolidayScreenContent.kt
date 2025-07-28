package pl.tinks.budgetbuddy.bankholiday

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.tinks.budgetbuddy.SectionHeader
import java.time.LocalDate

@Composable
fun BankHolidayScreenContent(bankHolidays: List<BankHoliday>, modifier: Modifier = Modifier) {
    val grouped = bankHolidays.groupBy { it.date.year }
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        grouped.forEach { (year, holidays) ->
            item {
                SectionHeader(year.toString())
            }
            itemsIndexed(holidays) { idx, holiday ->
                BankHolidayListItem(holiday)
                if (idx < holidays.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun BankHolidayScreenContentPreview() {
    BankHolidayScreenContent(
        listOf(
            bankHoliday,
            bankHoliday.copy(date = LocalDate.now().plusDays(1)),
            bankHoliday.copy(date = LocalDate.now().plusMonths(1)),
            bankHoliday.copy(date = LocalDate.now().plusYears(1))
        )
    )
}

val bankHoliday = BankHoliday("England", "Preview Text", LocalDate.now())
