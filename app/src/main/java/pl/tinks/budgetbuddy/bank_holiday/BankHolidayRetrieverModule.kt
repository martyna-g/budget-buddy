package pl.tinks.budgetbuddy.bank_holiday

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BankHolidayRetrieverModule {

    @Singleton
    @Provides
    fun provideBankHolidayRetriever(
        apiService: ApiService, apiMapper: ApiMapper, @ApplicationContext context: Context
    ): BankHolidayRetriever {
        return BankHolidayRetriever(apiService, apiMapper, context, Dispatchers.IO)
    }
}
