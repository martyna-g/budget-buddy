package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.DeletePaymentAndCleanupUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.list.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import java.time.LocalDateTime
import java.util.UUID

class DeletePaymentAndCleanupUseCaseTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var paymentScheduler: PaymentScheduler
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var useCase: DeletePaymentAndCleanupUseCase

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        paymentScheduler = Mockito.mock(PaymentScheduler::class.java)
        notificationScheduler = Mockito.mock(NotificationScheduler::class.java)
        useCase = DeletePaymentAndCleanupUseCase(
            paymentRepository, paymentScheduler, notificationScheduler
        )
    }

    @Test
    fun `deletes payment cancels recurring payment and notification`() = runBlocking {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.DAILY
        )

        useCase(payment)

        verify(paymentRepository).deletePayment(payment)
        verify(paymentScheduler).cancelRecurringPayment(payment)
        verify(notificationScheduler).cancelNotification(payment)
    }
}
