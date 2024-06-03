package pl.tinks.budgetbuddy.bank_holiday

import java.time.LocalDate

data class BankHoliday(
    val region: String,
    val title: String,
    val date: LocalDate,
)
