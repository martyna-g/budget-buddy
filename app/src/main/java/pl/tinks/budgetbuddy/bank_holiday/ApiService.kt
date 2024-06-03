package pl.tinks.budgetbuddy.bank_holiday

import retrofit2.http.GET

interface ApiService {
    @GET("bank-holidays.json")
    suspend fun getBankHolidays(): BankHolidayResponse
}
