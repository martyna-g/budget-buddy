package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.UndoMoveToHistoryUseCase
import java.time.LocalDateTime
import java.util.UUID

class UndoMoveToHistoryUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var useCase: UndoMoveToHistoryUseCase

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        useCase = UndoMoveToHistoryUseCase(paymentRepository)
    }

    @Test
    fun `marks payment as not completed by updating repository`() = runBlocking {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.DAILY
        )

        useCase(payment)

        verify(paymentRepository).updatePayment(payment.copy(paymentCompleted = false))
    }
}
