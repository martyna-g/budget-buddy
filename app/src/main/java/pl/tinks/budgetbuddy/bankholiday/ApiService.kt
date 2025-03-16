package pl.tinks.budgetbuddy.bankholiday

import retrofit2.http.GET

interface ApiService {
    @GET("bank-holidays.json")
    suspend fun getBankHolidays(): BankHolidayResponse
}
