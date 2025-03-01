package pl.tinks.budgetbuddy

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Dispatchers
import pl.tinks.budgetbuddy.bank_holiday.ApiMapper
import pl.tinks.budgetbuddy.bank_holiday.ApiService
import pl.tinks.budgetbuddy.bank_holiday.BankHolidayRetriever
import pl.tinks.budgetbuddy.bank_holiday.BankHolidayRetrieverModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [BankHolidayRetrieverModule::class]
)
object TestBankHolidayRetrieverModule {

    @Provides
    @Singleton
    fun provideBankHolidayRetriever(
        apiService: ApiService, apiMapper: ApiMapper, @ApplicationContext context: Context
    ): BankHolidayRetriever {
        return BankHolidayRetriever(apiService, apiMapper, context, Dispatchers.Unconfined)
    }
}
