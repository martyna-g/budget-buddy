package pl.tinks.budgetbuddy

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentDao
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class PaymentListUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var database: BudgetBuddyDatabase

    @Inject
    lateinit var paymentDao: PaymentDao

    private lateinit var overduePayment: Payment
    private lateinit var upcomingPayment: Payment


    @Before
    fun setUp() {
        disableAnimations()
        hiltRule.inject()

        overduePayment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
        upcomingPayment = overduePayment.copy(date = overduePayment.date.plusDays(1))

        ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun paymentListScreen_opensSuccessfully() {
        onView(withId(R.id.recyclerview_payment_list)).check(matches(isDisplayed()))
    }

    @Test
    fun addPayment_displaysPaymentInList() = runTest {
        onView(withId(R.id.fab_payment_list)).perform(click())

        onView(withId(R.id.text_input_edit_text_payment_title)).perform(replaceText(upcomingPayment.title))

        onView(withId(R.id.text_input_edit_text_payment_amount)).perform(replaceText(upcomingPayment.amount.toString()))

        onView(withId(R.id.text_input_edit_text_payment_date)).perform(
            replaceText(
                upcomingPayment.date.toLocalDate().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ).toString()
            )
        )

        onView(withId(R.id.autocomplete_textview_payment_frequency)).perform(
            replaceText(
                upcomingPayment.frequency.name.lowercase().replaceFirstChar { it.uppercaseChar() })
        )

        onView(withId(R.id.action_save)).perform(click())

        onView(withId(R.id.recyclerview_payment_list)).check(
            matches(
                hasDescendant(
                    withText(
                        upcomingPayment.title
                    )
                )
            )
        )
    }

    @Test
    fun navigateToPaymentDetails_opensPaymentDetailsScreen() = runTest {
        paymentDao.addPayment(overduePayment)

        onView(withId(R.id.recyclerview_payment_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )

        onView(withId(R.id.button_item_info_payment_due)).perform(click())

        onView(withId(R.id.text_input_edit_text_payment_title)).check(matches(isDisplayed()))
    }

    @Test
    fun movePaymentToHistory_movesPayment_whenPaymentIsOverdue() = runTest {
        paymentDao.addPayment(overduePayment)

        onView(withId(R.id.recyclerview_payment_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )

        onView(withId(R.id.layout_item_action_buttons_payment_due)).check(matches(isDisplayed()))

        onView(withId(R.id.button_item_move_to_history)).perform(click())

        onView(withText("Test Payment")).check(doesNotExist())

        onView(withId(R.id.payment_history_fragment)).perform(click())

        onView(withId(R.id.recyclerview_payment_history)).check(matches(hasDescendant(withText("Test Payment"))))
    }

    @Test
    fun deletePayment_removesPaymentFromList() = runTest {
        val paymentToDelete = upcomingPayment

        paymentDao.addPayment(paymentToDelete)

        onView(withId(R.id.recyclerview_payment_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )

        onView(withId(R.id.button_item_delete)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        onView(withText(R.string.yes)).perform(click())

        onView(withId(R.id.recyclerview_payment_list)).check(matches(not(hasDescendant(withText("Test Payment")))))
    }

    @Test
    fun editPayment_updatesPaymentDetails() = runTest {
        paymentDao.addPayment(upcomingPayment)

        onView(withId(R.id.recyclerview_payment_list)).check(matches(isDisplayed())).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
        )
        onView(withId(R.id.layout_item_action_buttons)).check(matches(isDisplayed()))

        onView(withId(R.id.button_item_edit)).perform(click())

        onView(withId(R.id.text_input_edit_text_payment_title)).check(matches(isDisplayed()))
            .perform(replaceText("Updated Payment"))

        onView(withId(R.id.text_input_edit_text_payment_title)).check(matches(withText("Updated Payment")))

        onView(withId(R.id.action_save)).check(matches(isDisplayed())).perform(click())

        onView(withText("Updated Payment")).check(matches(isDisplayed()))

        onView(withText("Test Payment")).check(doesNotExist())
    }


    private fun disableAnimations() {
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 0")
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 0")
        uiAutomation.executeShellCommand("settings put global window_animation_scale 0")
    }
}
