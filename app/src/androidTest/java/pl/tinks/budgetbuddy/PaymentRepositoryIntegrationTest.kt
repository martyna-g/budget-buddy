package pl.tinks.budgetbuddy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import pl.tinks.budgetbuddy.payment.PaymentRepositoryImpl
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class PaymentRepositoryIntegrationTest {
    private lateinit var database: BudgetBuddyDatabase
    private lateinit var paymentDao: PaymentDao
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var payment: Payment
    private val paymentId = UUID.randomUUID()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), BudgetBuddyDatabase::class.java
        ).build()
        paymentDao = database.getPaymentDao()
        paymentRepository = PaymentRepositoryImpl(paymentDao, UnconfinedTestDispatcher())

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
