package pl.tinks.budgetbuddy

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.bankholiday.BankHoliday
import pl.tinks.budgetbuddy.bankholiday.BankHolidayListItem
import pl.tinks.budgetbuddy.bankholiday.BankHolidayScreenContent
import pl.tinks.budgetbuddy.bankholiday.NextBankHolidaySection
import java.time.LocalDate

class BankHolidayUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bankHolidayScreenContent_displaysBankHolidayList() {
        val bankHolidays = listOf(
            BankHoliday("England and Wales", "Test BH", LocalDate.of(2025, 1, 1)),
            BankHoliday("England and Wales", "Test BH 2", LocalDate.of(2025, 12, 1)),
        )

        composeTestRule.setContent {
            BankHolidayScreenContent(bankHolidays = bankHolidays)
        }

        composeTestRule.onNodeWithText("2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test BH").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test BH 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("01 Jan").assertIsDisplayed()
        composeTestRule.onNodeWithText("01 Dec").assertIsDisplayed()
    }

    @Test
    fun nextBankHolidaySection_displaysDateAndTitle() {
        val bankHoliday = BankHoliday(
            date = LocalDate.of(2025, 1, 1), title = "Test BH", region = "England and Wales"
        )

        composeTestRule.setContent {
            NextBankHolidaySection(bankHoliday)
        }

        composeTestRule.onNodeWithText("01 January").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test BH").assertIsDisplayed()
    }

    @Test
    fun bankHolidayScreenContent_groupsByYear() {
        val bankHolidays = listOf(
            BankHoliday("England and Wales", "Test BH", LocalDate.of(2025, 1, 1)),
            BankHoliday("England and Wales", "Test BH 2", LocalDate.of(2026, 12, 1)),
        )

        composeTestRule.setContent {
            BankHolidayScreenContent(bankHolidays = bankHolidays)
        }

        composeTestRule.onNodeWithText("2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("2026").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test BH").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test BH 2").assertIsDisplayed()
    }

    @Test
    fun bankHolidayListItem_displaysCorrectDayOfWeek() {
        val bankHoliday = BankHoliday(
            "England and Wales", "Test BH", LocalDate.of(2025, 1, 1)
        )
        composeTestRule.setContent {
            BankHolidayListItem(bankHoliday)
        }
        composeTestRule.onNodeWithText("Wednesday").assertIsDisplayed()
    }

    @Test
    fun bankHolidayScreenContent_emptyList_showsNothing() {
        composeTestRule.setContent {
            BankHolidayScreenContent(bankHolidays = listOf())
        }
        composeTestRule.onAllNodesWithText("2025").assertCountEquals(0)
    }
}
