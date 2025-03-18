package pl.tinks.budgetbuddy

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class PaymentDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: BudgetBuddyDatabase

    @Inject
    lateinit var paymentDao: PaymentDao

    private lateinit var payment: Payment
    private lateinit var payments: List<Payment>
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

        payments = listOf(
            payment,
            payment.copy(id = UUID.randomUUID(), title = "Test Payment 2"),
            payment.copy(id = UUID.randomUUID(), title = "Test Payment 3")
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllPayments_returnsAllPayments() = runTest {
        payments.forEach { paymentDao.addPayment(it) }

        val result = paymentDao.getAllPayments().first()
        val expectedResult = payments.toTypedArray()

        assertThat(result, containsInAnyOrder(*expectedResult))
    }

    @Test
    fun getPaymentById_returnsCorrectPayment() = runTest {
        paymentDao.addPayment(payment)

        val paymentById = paymentDao.getPaymentById(paymentId)
        assertThat(paymentById, `is`(payment))
    }

    @Test
    fun updatePayment_updatesPaymentInDatabase() = runTest {
        paymentDao.addPayment(payment)

        val updatedPayment = payment.copy(title = "Updated Title")

        paymentDao.updatePayment(updatedPayment)

        val result = paymentDao.getPaymentById(paymentId)

        assertThat(result?.title, `is`("Updated Title"))
    }

    @Test
    fun deletePayment_removesPaymentFromDatabase() = runTest {
        paymentDao.addPayment(payment)

        val resultBeforeDelete = paymentDao.getPaymentById(paymentId)

        assertThat(resultBeforeDelete, `is`(payment))

        paymentDao.deletePayment(payment)

        val resultAfterDelete = paymentDao.getPaymentById(paymentId)

        assertThat(resultAfterDelete, `is`(nullValue()))
    }

    @Test
    fun deletePayments_removesAllSpecifiedPaymentsFromDatabase() = runTest {
        payments.forEach { paymentDao.addPayment(it) }

        val resultBeforeDelete = paymentDao.getAllPayments().first()

        assertThat(resultBeforeDelete, containsInAnyOrder(*payments.toTypedArray()))

        paymentDao.deletePayments(payments)

        val resultAfterDelete = paymentDao.getAllPayments().first()

        assertThat(resultAfterDelete, `is`(emptyList()))
    }
}
