package pl.tinks.budgetbuddy.bank_holiday

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import pl.tinks.budgetbuddy.Result

class BankHolidayRetriever @Inject constructor(
    private val apiService: ApiService,
    private val apiMapper: ApiMapper,
    @ApplicationContext private val context: Context,
) {

    suspend fun getBankHolidays(): Result<List<BankHoliday>> {
        return try {
            val response = apiService.getBankHolidays()
            val bankHolidays = apiMapper.toBankHoliday(response, context)
            Result.Success(bankHolidays)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
