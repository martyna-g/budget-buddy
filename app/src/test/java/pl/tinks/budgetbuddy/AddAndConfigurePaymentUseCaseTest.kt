package pl.tinks.budgetbuddy

import kotlinx.coroutines.test.runTest
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.list.AddAndConfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.notification.NotificationScheduler
import pl.tinks.budgetbuddy.payment.list.PaymentScheduler
import java.time.LocalDateTime
import java.util.UUID

class AddAndConfigurePaymentUseCaseTest {

    private lateinit var useCase: AddAndConfigurePaymentUseCase
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var paymentScheduler: PaymentScheduler
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var payment: Payment

    @Before
    fun setUp() {
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        paymentScheduler = Mockito.mock(PaymentScheduler::class.java)
        notificationScheduler = Mockito.mock(NotificationScheduler::class.java)

        useCase = AddAndConfigurePaymentUseCase(
            paymentRepository, paymentScheduler, notificationScheduler
        )

        payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
    }

    @Test
    fun `adds payment schedules recurring payment and notification`() = runTest {
        useCase(payment)

        verify(paymentRepository).addPayment(payment)
        verify(paymentScheduler).scheduleRecurringPayment(payment)
        verify(notificationScheduler).scheduleNotification(payment)
    }
}
