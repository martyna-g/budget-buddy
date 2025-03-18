package pl.tinks.budgetbuddy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryUiState
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryViewModel
import java.time.LocalDateTime
import java.util.UUID

class PaymentHistoryViewModelTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var payment: Payment

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock()
        payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY,
            paymentCompleted = true
        )
    }

    @Test
    fun `uiState reflects Success when repository returns data`() = runTest {
        Mockito.`when`(paymentRepository.getAllPayments())
            .thenReturn(flowOf(Result.Success(listOf(payment))))

        val viewModel = PaymentHistoryViewModel(paymentRepository)

        val result = viewModel.uiState.first()

        assertThat(result, `is`(PaymentHistoryUiState.Success(listOf(payment))))
    }

    @Test
    fun `deleteSelectedPayments calls deletePayments on repository`() = runTest {
        val viewModel = PaymentHistoryViewModel(paymentRepository)
        val payments = listOf(payment)

        viewModel.deleteSelectedPayments(payments)

        verify(paymentRepository).deletePayments(payments)
    }
}
