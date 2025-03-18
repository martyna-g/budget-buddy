package pl.tinks.budgetbuddy

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
class PaymentRepositoryIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: BudgetBuddyDatabase

    @Inject
    lateinit var paymentDao: PaymentDao

    @Inject
    lateinit var paymentRepository: PaymentRepository

    private lateinit var payment: Payment
    private val paymentId = UUID.randomUUID()

    @Before
    fun setUp() {
        hiltRule.inject()

        payment = Payment(
            paymentId,
            "Test Payment",
            Money.of(CurrencyUnit.GBP, 10.00),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            PaymentFrequency.DAILY
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllPayments_emitsSuccess_whenDataIsAvailable() = runTest {
        paymentRepository.addPayment(payment)

        val result = paymentRepository.getAllPayments().first()
        assertThat(result, `is`(Result.Success(listOf(payment))))
    }

    @Test
    fun getAllPayments_emitsSuccessWithEmptyList_whenNoDataIsAvailable() = runTest {
        val result = paymentRepository.getAllPayments().first()
        assertThat(result, `is`(Result.Success(emptyList())))
    }

    @Test
    fun getPaymentById_retrievesCorrectPayment() = runTest {
        paymentRepository.addPayment(payment)

        val result = paymentRepository.getPaymentById(paymentId)
        assertThat(result, `is`(payment))
    }

    @Test
    fun addPayment_addsPaymentToDatabase() = runTest {
        paymentRepository.addPayment(payment)

        val result = paymentDao.getPaymentById(paymentId)
        assertThat(result, `is`(payment))
    }

    @Test
    fun updatePayment_updatesPaymentInDatabase() = runTest {
        val updatedPayment = payment.copy(title = "Updated Payment")

        paymentRepository.addPayment(payment)
        paymentRepository.updatePayment(updatedPayment)

        val result = paymentRepository.getPaymentById(paymentId)

        assertThat(result, `is`(updatedPayment))
    }

    @Test
    fun deletePayment_removesPaymentFromDatabase() = runTest {
        paymentRepository.addPayment(payment)

        val beforeDelete = paymentRepository.getPaymentById(paymentId)
        assertThat(beforeDelete, `is`(payment))

        paymentRepository.deletePayment(payment)

        val afterDelete = paymentRepository.getPaymentById(paymentId)
        assertThat(afterDelete, `is`(nullValue()))
    }

    @Test
    fun deletePayment_doesNotThrowException_whenPaymentDoesNotExist() = runTest {
        assertThat (
            runCatching { paymentRepository.deletePayment(payment) }.exceptionOrNull(),
            `is`(nullValue())
        )
    }
}
