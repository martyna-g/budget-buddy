package pl.tinks.budgetbuddy.bankholiday

import java.time.LocalDate

data class BankHoliday(
    val region: String,
    val title: String,
    val date: LocalDate,
)
