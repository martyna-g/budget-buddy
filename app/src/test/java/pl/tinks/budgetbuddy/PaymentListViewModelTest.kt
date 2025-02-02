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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.AddAndConfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.DeletePaymentAndCleanupUseCase
import pl.tinks.budgetbuddy.payment.GetPaymentByIdUseCase
import pl.tinks.budgetbuddy.payment.MoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.UndoMoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.UpdateAndReconfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.list.PaymentListViewModel
import pl.tinks.budgetbuddy.payment.list.PaymentUiState
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
    private lateinit var getPaymentByIdUseCase: GetPaymentByIdUseCase

    @Mock
    private lateinit var updateAndReconfigurePaymentUseCase: UpdateAndReconfigurePaymentUseCase

    @Mock
    private lateinit var addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase

    @Mock
    private lateinit var deletePaymentAndCleanupUseCase: DeletePaymentAndCleanupUseCase

    @Mock
    private lateinit var moveToHistoryUseCase: MoveToHistoryUseCase

    @Mock
    private lateinit var undoMoveToHistoryUseCase: UndoMoveToHistoryUseCase

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

        assertThat(viewModel.uiState.first(), `is`(PaymentUiState.Success(payments)))
    }

    @Test
    fun `addPayment calls addAndConfigurePaymentUseCase`() = runTest {
        viewModel.addPayment(payment)

        verify(addAndConfigurePaymentUseCase).invoke(payment)
    }

    @Test
    fun `updatePayment calls updateAndReconfigurePaymentUseCase`() = runTest {
        viewModel.updatePayment(payment)

        verify(updateAndReconfigurePaymentUseCase).invoke(payment)
    }

    @Test
    fun `moveToHistory calls moveToHistoryUseCase`() = runTest {
        viewModel.moveToHistory(payment)

        verify(moveToHistoryUseCase).invoke(payment)
    }

    @Test
    fun `undoMoveToHistory calls undoMoveToHistoryUseCase`() = runTest {
        viewModel.undoMoveToHistory(payment)

        verify(undoMoveToHistoryUseCase).invoke(payment)
    }

    @Test
    fun `deletePayment calls deletePaymentAndCleanupUseCase`() = runTest {
        viewModel.deletePayment(payment)

        verify(deletePaymentAndCleanupUseCase).invoke(payment)
    }

    private fun createViewModel(): PaymentListViewModel = PaymentListViewModel(
        repository,
        getPaymentByIdUseCase,
        updateAndReconfigurePaymentUseCase,
        addAndConfigurePaymentUseCase,
        deletePaymentAndCleanupUseCase,
        moveToHistoryUseCase,
        undoMoveToHistoryUseCase
    )
}
