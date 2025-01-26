package pl.tinks.budgetbuddy

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.PaymentRepositoryImpl
import java.time.LocalDateTime
import java.util.UUID

class PaymentRepositoryTest {

    private lateinit var paymentDao: PaymentDao
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var payment: Payment

    @Before
    fun setUp() {
        paymentDao = Mockito.mock(PaymentDao::class.java)
        paymentRepository = PaymentRepositoryImpl(paymentDao)
        payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
    }

    @Test
    fun `getAllPayments emits success when dao returns data`() = runBlocking {
        val payments = listOf(payment)
        Mockito.`when`(paymentDao.getAllPayments()).thenReturn(flowOf(payments))

        val result = paymentRepository.getAllPayments().first()

        verify(paymentDao).getAllPayments()
        assertThat(result, `is`(Result.Success(payments)))
    }

    @Test
    fun `getAllPayments emits success with empty list when dao returns no data`() = runBlocking {
        Mockito.`when`(paymentDao.getAllPayments()).thenReturn(flowOf(emptyList()))

        val result = paymentRepository.getAllPayments().first()

        verify(paymentDao).getAllPayments()
        assertThat(result, `is`(Result.Success(emptyList())))
    }

    @Test
    fun `getAllPayments emits error when dao throws exception`() = runBlocking {
        val exception = RuntimeException("Database error")
        val flowWithError = flow<List<Payment>> { throw exception }
        Mockito.`when`(paymentDao.getAllPayments()).thenReturn(flowWithError)

        val result = paymentRepository.getAllPayments().first()

        verify(paymentDao).getAllPayments()
        assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun `getPaymentById retrieves payment by id`() = runBlocking {
        val paymentId = payment.id

        Mockito.`when`(paymentDao.getPaymentById(paymentId)).thenReturn(payment)

        val retrievedPayment = paymentRepository.getPaymentById(paymentId)

        verify(paymentDao).getPaymentById(paymentId)
        assertThat(retrievedPayment, `is`(payment))
    }

    @Test
    fun `getPaymentById returns null when no payment found`() = runBlocking {
        val paymentId = UUID.randomUUID()
        Mockito.`when`(paymentDao.getPaymentById(paymentId)).thenReturn(null)

        val result = paymentRepository.getPaymentById(paymentId)

        verify(paymentDao).getPaymentById(paymentId)
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun `addPayment calls addPayment on dao`() = runBlocking {
        paymentRepository.addPayment(payment)

        verify(paymentDao).addPayment(payment)
    }

    @Test
    fun `updatePayment calls updatePayment on dao`() = runBlocking {
        val updatedPayment = payment.copy(title = "Updated Payment")

        paymentRepository.updatePayment(updatedPayment)

        verify(paymentDao).updatePayment(updatedPayment)
    }

    @Test
    fun `deletePayment calls deletePayment on dao`() = runBlocking {
        paymentRepository.deletePayment(payment)

        verify(paymentDao).deletePayment(payment)
    }
}
