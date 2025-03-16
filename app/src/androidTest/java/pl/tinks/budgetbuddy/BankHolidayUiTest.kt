package pl.tinks.budgetbuddy

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.bankholiday.ApiService
import pl.tinks.budgetbuddy.bankholiday.BankHolidayRetriever
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltAndroidTest
class BankHolidayUiTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var retriever: BankHolidayRetriever

    @Inject
    lateinit var apiService: ApiService

    private lateinit var viewPagerReference: ViewPager2
    private lateinit var idlingResource: ViewPager2IdlingResource

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun openBankHolidayFragment_displaysBankHolidaysList(): Unit = runTest {
        enqueueMockResponse(1)

        onView(withId(R.id.bank_holiday_fragment)).perform(click())

        onView(withId(R.id.recyclerView_bank_holiday)).check(matches(isDisplayed()))

        onView(
            allOf(
                withText("Test England Holiday"), withId(R.id.text_view_next_bank_holiday_title)
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withText("Test England Holiday"), withId(R.id.text_view_item_bank_holiday_title)
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun swipeViewPager_changesDisplayedCountry() {
        enqueueMockResponse(3)

        onView(withId(R.id.bank_holiday_fragment)).perform(click())

        activityRule.scenario.onActivity { activity ->
            viewPagerReference = activity.findViewById(R.id.view_pager_bank_holiday)
            idlingResource = ViewPager2IdlingResource(viewPagerReference)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(
            allOf(
                withText("Test England Holiday"), withId(R.id.text_view_next_bank_holiday_title)
            )
        ).check(matches(isDisplayed()))

        onView(withId(R.id.view_pager_bank_holiday)).perform(swipeLeft())

        onView(
            allOf(
                withText("Test Scotland Holiday"), withId(R.id.text_view_next_bank_holiday_title)
            )
        ).check(matches(isDisplayed()))

        onView(withId(R.id.view_pager_bank_holiday)).perform(swipeLeft())

        onView(
            allOf(
                withText("Test NI Holiday"), withId(R.id.text_view_next_bank_holiday_title)
            )
        ).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun apiError_showsErrorDialog() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        onView(withId(R.id.bank_holiday_fragment)).perform(click())

        onView(withText(R.string.bank_holiday_loading_error_message)).check(matches(isDisplayed()))

        onView(withText(R.string.dialog_ok)).check(matches(isDisplayed()))

        onView(withText(R.string.dialog_ok)).perform(click())
    }

    private fun enqueueMockResponse(times: Int) {
        repeat(times) {
            mockWebServer.enqueue(
                MockResponse().setBody(getMockBankHolidayResponse()).setResponseCode(200)
            )
        }
    }

    private fun getMockBankHolidayResponse(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val testDate = LocalDate.now().plusYears(1).format(formatter)
        return """
    {
        "england-and-wales": {
            "division": "england-and-wales",
            "events": [
                {"title": "Test England Holiday", "date": "$testDate", "notes": "", "bunting": false}
            ]
        },
        "scotland": {
            "division": "scotland",
            "events": [
                {"title": "Test Scotland Holiday", "date": "$testDate", "notes": "", "bunting": false}
            ]
        }, 
        "northern-ireland": {
            "division": "northern-ireland",
            "events": [
                {"title": "Test NI Holiday", "date": "$testDate", "notes": "", "bunting": false}
            ]
        }
    }
    """.trimIndent()
    }
}
