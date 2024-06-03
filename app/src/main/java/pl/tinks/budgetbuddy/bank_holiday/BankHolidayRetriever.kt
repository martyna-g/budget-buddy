package pl.tinks.budgetbuddy.bank_holiday

import javax.inject.Inject

class BankHolidayRetriever @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getBankHolidays(): BankHolidayResponse {
        return apiService.getBankHolidays()
    }

}
