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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.list.DeletePaymentAndCleanupUseCase
import pl.tinks.budgetbuddy.payment.list.MoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.list.PaymentListViewModel
import pl.tinks.budgetbuddy.payment.list.PaymentUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class PaymentListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var payment: Payment
    private lateinit var viewModel: PaymentListViewModel

    @Mock
    private lateinit var repository: PaymentRepository

    @Mock
    private lateinit var deletePaymentAndCleanupUseCase: DeletePaymentAndCleanupUseCase

    @Mock
    private lateinit var moveToHistoryUseCase: MoveToHistoryUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )

        viewModel = createViewModel()
    }

    @Test
    fun `getAllPayments emits Success when repository returns data`() = runTest {
        val payments = listOf(payment)
        `when`(repository.getAllPayments()).thenReturn(flowOf(Result.Success(payments)))

        val viewModel = createViewModel()

        val grouped = payments.filter { !it.paymentCompleted }
            .sortedBy { it.date }
            .groupBy { payment ->
                when {
                    payment.date.toLocalDate() < LocalDate.now() -> R.string.overdue_payments
                    payment.date.toLocalDate() == LocalDate.now() -> R.string.payments_due_today
                    else -> R.string.upcoming_payments
                }
            }
        val expectedListItems = grouped.flatMap { (categoryResId, paymentsInCategory) ->
            listOf(PaymentListItem.StaticHeader(categoryResId)) + paymentsInCategory.map {
                PaymentListItem.PaymentEntry(it)
            }
        }
        val expected = PaymentUiState.Success(expectedListItems)

        val actual = viewModel.uiState
            .dropWhile { it is PaymentUiState.Loading }
            .first()

        assertThat(actual, `is`(expected))
    }

    @Test
    fun `moveToHistory calls moveToHistoryUseCase`() = runTest {
        viewModel.moveToHistory(payment)

        verify(moveToHistoryUseCase).invoke(payment)
    }

    @Test
    fun `deletePayment calls deletePaymentAndCleanupUseCase`() = runTest {
        viewModel.deletePayment(payment)

        verify(deletePaymentAndCleanupUseCase).invoke(payment)
    }

    private fun createViewModel(): PaymentListViewModel = PaymentListViewModel(
        repository,
        deletePaymentAndCleanupUseCase,
        moveToHistoryUseCase,
    )
}
