package pl.tinks.budgetbuddy

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.bankholiday.ApiMapper
import pl.tinks.budgetbuddy.bankholiday.ApiService
import pl.tinks.budgetbuddy.bankholiday.BankHoliday
import pl.tinks.budgetbuddy.bankholiday.BankHolidayEvent
import pl.tinks.budgetbuddy.bankholiday.BankHolidayRegion
import pl.tinks.budgetbuddy.bankholiday.BankHolidayResponse
import pl.tinks.budgetbuddy.bankholiday.BankHolidayRetriever
import java.time.LocalDate

class BankHolidayRetrieverTest {

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var apiMapper: ApiMapper

    @Mock
    lateinit var context: Context

    private lateinit var retriever: BankHolidayRetriever

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        retriever = BankHolidayRetriever(apiService, apiMapper, context, Dispatchers.Unconfined)
    }

    @Test
    fun `getBankHolidays returns success when API call succeeds`(): Unit = runTest {
        val response = BankHolidayResponse(
            englandAndWales = BankHolidayRegion(
                "england-and-wales", listOf(BankHolidayEvent("New Year", "2025-01-01", "", true))
            ),
            scotland = BankHolidayRegion("scotland", emptyList()),
            northernIreland = BankHolidayRegion("northern-ireland", emptyList())
        )

        val expectedHolidays = mutableListOf(
            BankHoliday(
                "England and Wales", "New Year", LocalDate.of(2025, 1, 1)
            )
        )

        Mockito.`when`(apiService.getBankHolidays()).thenReturn(response)
        Mockito.`when`(apiMapper.toBankHoliday(response, context)).thenReturn(expectedHolidays)

        val result = retriever.getBankHolidays()

        verify(apiService).getBankHolidays()
        verify(apiMapper).toBankHoliday(response, context)
        assertThat(result, `is`(Result.Success(expectedHolidays)))
        assertThat((result as Result.Success).data, `is`(expectedHolidays))
    }

    @Test
    fun `getBankHolidays returns error when API call fails`() = runTest {
        val exception = RuntimeException("Network error")

        Mockito.`when`(apiService.getBankHolidays()).thenThrow(exception)

        val result = retriever.getBankHolidays()

        val resultError = result as Result.Error
        val resultException = resultError.e

        verify(apiService).getBankHolidays()
        assertThat(resultException::class, `is`(RuntimeException::class))
        assertThat(resultException.message, `is`(exception.message))
    }
}
