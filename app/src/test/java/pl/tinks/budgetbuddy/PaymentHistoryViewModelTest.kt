package pl.tinks.budgetbuddy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.dropWhile
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
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryUiState
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryViewModel
import pl.tinks.budgetbuddy.payment.list.UndoMoveToHistoryUseCase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class PaymentHistoryViewModelTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var undoMoveToHistoryUseCase: UndoMoveToHistoryUseCase
    private lateinit var payment: Payment

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock()
        undoMoveToHistoryUseCase = Mockito.mock()
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
        val payments = listOf(payment)

        Mockito.`when`(paymentRepository.getAllPayments())
            .thenReturn(flowOf(Result.Success(payments)))

        val viewModel = PaymentHistoryViewModel(paymentRepository, undoMoveToHistoryUseCase)

        val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
        val historyItems = payments.filter { it.paymentCompleted }.sortedByDescending { it.date }
            .groupBy { it.date.format(formatter) }.flatMap { (month, paymentsInMonth) ->
                listOf(PaymentListItem.DynamicHeader(month)) + paymentsInMonth.map {
                    PaymentListItem.PaymentEntry(it)
                }
            }

        val expected = PaymentHistoryUiState.Success(historyItems)

        val result = viewModel.uiState.dropWhile { it is PaymentHistoryUiState.Loading }.first()

        assertThat(result, `is`(expected))
    }

    @Test
    fun `deletePayment calls deletePayment on repository`() = runTest {
        val viewModel = PaymentHistoryViewModel(paymentRepository, undoMoveToHistoryUseCase)

        viewModel.deletePayment(payment)

        verify(paymentRepository).deletePayment(payment)
    }

    @Test
    fun `undoMoveToHistory calls undoMoveToHistoryUseCase`() = runTest {
        val viewModel = PaymentHistoryViewModel(paymentRepository, undoMoveToHistoryUseCase)

        viewModel.undoMoveToHistory(payment)

        verify(undoMoveToHistoryUseCase).invoke(payment)
    }
}
