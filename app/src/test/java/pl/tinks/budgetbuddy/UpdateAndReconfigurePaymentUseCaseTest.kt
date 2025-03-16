package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.list.UpdateAndReconfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.notification.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import java.time.LocalDateTime
import java.util.UUID

class UpdateAndReconfigurePaymentUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var paymentScheduler: PaymentScheduler
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var useCase: UpdateAndReconfigurePaymentUseCase
    private lateinit var originalPayment: Payment

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        paymentScheduler = Mockito.mock(PaymentScheduler::class.java)
        notificationScheduler = Mockito.mock(NotificationScheduler::class.java)
        useCase = UpdateAndReconfigurePaymentUseCase(
            paymentRepository, paymentScheduler, notificationScheduler
        )
        originalPayment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.0),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY,
            notificationEnabled = true
        )
    }

    @Test
    fun `updates payment schedules recurring payment and notification when frequency changed to recurring`() =
        runBlocking {
            val updatedPayment = originalPayment.copy(frequency = PaymentFrequency.DAILY)

            Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
                .thenReturn(originalPayment)

            useCase(updatedPayment)

            verify(paymentRepository).updatePayment(updatedPayment)
            verify(paymentScheduler).updateRecurringPayment(updatedPayment)
            verify(notificationScheduler).updateNotification(updatedPayment)
        }

    @Test
    fun `cancels recurring payment and updates notification when frequency changed to single`() =
        runBlocking {
            val updatedPayment = originalPayment.copy(frequency = PaymentFrequency.SINGLE_PAYMENT)

            Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
                .thenReturn(originalPayment)

            useCase(updatedPayment)

            verify(paymentRepository).updatePayment(updatedPayment)
            verify(paymentScheduler).cancelRecurringPayment(updatedPayment)
            verify(notificationScheduler).updateNotification(updatedPayment)
        }

    @Test
    fun `schedules recurring payment and updates notification when frequency changed from single`() =
        runBlocking {
            val originalPayment = Payment(
                id = UUID.randomUUID(),
                title = "Test Payment",
                amount = Money.of(CurrencyUnit.GBP, 10.0),
                date = LocalDateTime.now(),
                frequency = PaymentFrequency.SINGLE_PAYMENT,
                notificationEnabled = true
            )
            val updatedPayment = originalPayment.copy(frequency = PaymentFrequency.DAILY)

            Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
                .thenReturn(originalPayment)

            useCase(updatedPayment)

            verify(paymentRepository).updatePayment(updatedPayment)
            verify(paymentScheduler).scheduleRecurringPayment(updatedPayment)
            verify(notificationScheduler).updateNotification(updatedPayment)
        }

    @Test
    fun `updates payment reschedules recurring payment and notification when date changed`() =
        runBlocking {
            val updatedPayment = originalPayment.copy(date = originalPayment.date.plusDays(1))

            Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
                .thenReturn(originalPayment)

            useCase(updatedPayment)

            verify(paymentRepository).updatePayment(updatedPayment)
            verify(paymentScheduler).updateRecurringPayment(updatedPayment)
            verify(notificationScheduler).updateNotification(updatedPayment)
        }

    @Test
    fun `updates payment and reschedules notification when title changed`() = runBlocking {
        val updatedPayment = originalPayment.copy(title = "Test Payment 2")

        Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
            .thenReturn(originalPayment)

        useCase(updatedPayment)

        verify(paymentRepository).updatePayment(updatedPayment)
        verify(paymentScheduler, never()).updateRecurringPayment(updatedPayment)
        verify(notificationScheduler).updateNotification(updatedPayment)
    }

    @Test
    fun `updates payment when amount changed`() = runBlocking {
        val updatedPayment = originalPayment.copy(amount = Money.of(CurrencyUnit.GBP, 0.00))

        Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
            .thenReturn(originalPayment)

        useCase(updatedPayment)

        verify(paymentRepository).updatePayment(updatedPayment)
        verify(paymentScheduler, never()).updateRecurringPayment(updatedPayment)
        verify(notificationScheduler, never()).updateNotification(updatedPayment)
    }

    @Test
    fun `cancels notification when turned off`() = runBlocking {
        val updatedPayment = originalPayment.copy(notificationEnabled = false)

        Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
            .thenReturn(originalPayment)

        useCase(updatedPayment)

        verify(paymentRepository).updatePayment(updatedPayment)
        verify(paymentScheduler, never()).updateRecurringPayment(updatedPayment)
        verify(notificationScheduler).cancelNotification(updatedPayment)
    }

    @Test
    fun `schedules notification when turned on`() = runBlocking {
        val originalPayment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.0),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY,
            notificationEnabled = false
        )
        val updatedPayment = originalPayment.copy(notificationEnabled = true)

        Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id))
            .thenReturn(originalPayment)

        useCase(updatedPayment)

        verify(paymentRepository).updatePayment(updatedPayment)
        verify(paymentScheduler, never()).updateRecurringPayment(updatedPayment)
        verify(notificationScheduler).scheduleNotification(updatedPayment)
    }

    @Test
    fun `throws exception when payment not found`(): Unit = runBlocking {
        val updatedPayment = originalPayment.copy(title = "Test Payment 2")
        Mockito.`when`(paymentRepository.getPaymentById(updatedPayment.id)).thenReturn(null)

        val exception = assertThrows(IllegalStateException::class.java) {
            runBlocking { useCase(updatedPayment) }
        }

        assertThat(
            exception.message,
            `is`("Payment with ID ${updatedPayment.id} not found. This indicates a bug in the app.")
        )

        verify(paymentRepository, never()).updatePayment(updatedPayment)
        verifyNoInteractions(paymentScheduler, notificationScheduler)
    }
}
