package pl.tinks.budgetbuddy.bankholiday

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import pl.tinks.budgetbuddy.Result

class BankHolidayRetriever @Inject constructor(
    private val apiService: ApiService,
    private val apiMapper: ApiMapper,
    @ApplicationContext private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun getBankHolidays(): Result<List<BankHoliday>> {
        return withContext(dispatcher) {
            try {
                val response = apiService.getBankHolidays()
                val bankHolidays = apiMapper.toBankHoliday(response, context)
                Result.Success(bankHolidays)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
