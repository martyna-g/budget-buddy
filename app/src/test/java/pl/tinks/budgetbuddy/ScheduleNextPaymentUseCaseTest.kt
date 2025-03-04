package pl.tinks.budgetbuddy

import kotlinx.coroutines.runBlocking
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import pl.tinks.budgetbuddy.payment.AddAndConfigurePaymentUseCase
import pl.tinks.budgetbuddy.payment.DeletePaymentAndCleanupUseCase
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.list.PaymentDateCalculator
import pl.tinks.budgetbuddy.payment.list.ScheduleNextPaymentUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

class ScheduleNextPaymentUseCaseTest {

    private lateinit var calculator: PaymentDateCalculator
    private lateinit var addAndConfigurePaymentUseCase: AddAndConfigurePaymentUseCase
    private lateinit var deletePaymentAndCleanupUseCase: DeletePaymentAndCleanupUseCase
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var useCase: ScheduleNextPaymentUseCase
    private lateinit var payment: Payment
    private lateinit var nextPaymentDate: LocalDate

    @Before
    fun setUp() {
        calculator = Mockito.mock(PaymentDateCalculator::class.java)
        addAndConfigurePaymentUseCase = Mockito.mock(AddAndConfigurePaymentUseCase::class.java)
        deletePaymentAndCleanupUseCase = Mockito.mock(DeletePaymentAndCleanupUseCase::class.java)
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        useCase =
            ScheduleNextPaymentUseCase(
                calculator,
                addAndConfigurePaymentUseCase,
                deletePaymentAndCleanupUseCase,
                paymentRepository
            )

        payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.DAILY
        )
        nextPaymentDate = payment.date.plusDays(1).toLocalDate()

        Mockito.`when`(
            calculator.calculateNextPaymentDate(
                payment.date.toLocalDate(), payment.frequency
            )
        ).thenReturn(nextPaymentDate)
    }

    @Test
    fun `calculates next payment date adds new payment and marks as scheduled`() = runBlocking {
        val generatedUuid = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val nextPayment = payment.copy(
            id = generatedUuid, date = LocalDateTime.of(nextPaymentDate, LocalTime.MIDNIGHT)
        )

        Mockito.mockStatic(UUID::class.java).use { mockedUuid ->
            mockedUuid.`when`<UUID> { UUID.randomUUID() }.thenReturn(generatedUuid)

            useCase(payment)

            verify(calculator).calculateNextPaymentDate(payment.date.toLocalDate(), payment.frequency)
            verify(addAndConfigurePaymentUseCase).invoke(nextPayment)
            verify(paymentRepository).updatePayment(payment.copy(isNextPaymentScheduled = true))
        }
    }

    @Test
    fun `does not mark payment as scheduled when AddAndConfigurePaymentUseCase throws exception`() =
        runBlocking {
            val generatedUuid = UUID.fromString("00000000-0000-0000-0000-000000000000")
            val nextPayment = payment.copy(
                id = generatedUuid, date = LocalDateTime.of(nextPaymentDate, LocalTime.MIDNIGHT)
            )

            Mockito.mockStatic(UUID::class.java).use { mockedUuid ->
                mockedUuid.`when`<UUID> { UUID.randomUUID() }.thenReturn(generatedUuid)

                useCase(payment)

                Mockito.`when`(addAndConfigurePaymentUseCase.invoke(nextPayment)).thenThrow(
                    CancellationException()
                )

                verify(calculator).calculateNextPaymentDate(payment.date.toLocalDate(), payment.frequency)
                verify(addAndConfigurePaymentUseCase).invoke(nextPayment)
                verify(paymentRepository, never()).updatePayment(payment.copy(paymentCompleted = true))
            }
        }
}
