package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.GetPaymentByIdUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.util.UUID

class GetPaymentByIdUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var useCase: GetPaymentByIdUseCase

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        useCase = GetPaymentByIdUseCase(paymentRepository)
    }

    @Test
    fun `returns payment when id exists`(): Unit = runBlocking {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )

        Mockito.`when`(paymentRepository.getPaymentById(payment.id)).thenReturn(payment)

        val result = useCase(payment.id)

        verify(paymentRepository).getPaymentById(payment.id)
        assertThat(payment, `is`(result))
    }

    @Test
    fun `throws IllegalStateException when payment is not found`() = runBlocking {
        val paymentId = UUID.randomUUID()
        Mockito.`when`(paymentRepository.getPaymentById(paymentId)).thenReturn(null)

        val exception = assertThrows(IllegalStateException::class.java) {
            runBlocking { useCase(paymentId) }
        }

        assertThat(
            exception.message,
            `is`("Payment with ID $paymentId not found. This indicates a bug in the app.")
        )
    }
}
