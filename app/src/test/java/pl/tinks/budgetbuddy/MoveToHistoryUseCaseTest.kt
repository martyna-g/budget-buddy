package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.list.MoveToHistoryUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.util.UUID

class MoveToHistoryUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var useCase: MoveToHistoryUseCase

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        useCase = MoveToHistoryUseCase(paymentRepository)
    }

    @Test
    fun `marks payment as completed by updating repository`() = runBlocking {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.DAILY
        )

        useCase(payment)

        verify(paymentRepository).updatePayment(payment.copy(paymentCompleted = true))
    }
}