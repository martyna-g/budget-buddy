package pl.tinks.budgetbuddy.bank_holiday

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BankHolidayRetriever @Inject constructor(
    private val apiService: ApiService,
    private val apiMapper: ApiMapper,
    @ApplicationContext private val context: Context,
) {

    suspend fun getBankHolidays(): List<BankHoliday> {
        val response = apiService.getBankHolidays()
        return apiMapper.toBankHoliday(response, context)
    }

}
