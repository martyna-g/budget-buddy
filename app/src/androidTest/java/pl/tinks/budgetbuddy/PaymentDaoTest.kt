package pl.tinks.budgetbuddy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@SmallTest
class PaymentDaoTest {

    private lateinit var database: BudgetBuddyDatabase
    private lateinit var paymentDao: PaymentDao
    private val paymentId = UUID.randomUUID()
    private lateinit var payment: Payment

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), BudgetBuddyDatabase::class.java
        ).build()

        paymentDao = database.getPaymentDao()

        payment = Payment(
            paymentId,
            "Rent",
            Money.of(CurrencyUnit.GBP, 111.11),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            PaymentFrequency.DAILY
        )

        runBlocking {
            paymentDao.addPayment(payment)
        }
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getById_returnsCorrectPayment() = runBlocking {
        val paymentById = paymentDao.getPaymentById(paymentId)
        MatcherAssert.assertThat(paymentById, CoreMatchers.equalTo(payment))
    }
}
