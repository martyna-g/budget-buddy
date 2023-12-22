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
import pl.tinks.budgetbuddy.payment.PaymentDatabase
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@SmallTest
class PaymentEntityReadWriteTest {

    private lateinit var database: PaymentDatabase
    private lateinit var paymentDao: PaymentDao
    private val paymentId = UUID.randomUUID()

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PaymentDatabase::class.java
        ).build()

        paymentDao = database.getPaymentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() = database.close()

    @Test
    @Throws(Exception::class)
    fun addPaymentAndGetById() = runBlocking {
        val payment = Payment(
            paymentId,
            "Rent",
            Money.of(CurrencyUnit.GBP, 111.11),
            LocalDateTime.now(),
            1
        )

        paymentDao.addPayment(payment)

        val paymentById = paymentDao.getPaymentById(paymentId)
        MatcherAssert.assertThat(paymentById, CoreMatchers.equalTo(payment))
    }
}
