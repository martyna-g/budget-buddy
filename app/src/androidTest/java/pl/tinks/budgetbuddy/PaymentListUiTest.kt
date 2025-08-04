package pl.tinks.budgetbuddy

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentListContent
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.list.PaymentFrequency
import pl.tinks.budgetbuddy.payment.list.PaymentListScreenContent
import pl.tinks.budgetbuddy.payment.list.PaymentUiState
import java.time.LocalDateTime
import java.util.UUID

class PaymentListUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun paymentListScreenContent_displaysAllPaymentsAndHeaders() {
        val overduePayment = Payment(
            id = UUID.randomUUID(),
            title = "Overdue Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now().minusDays(1),
            frequency = PaymentFrequency.MONTHLY
        )
        val paymentDueToday = Payment(
            id = UUID.randomUUID(),
            title = "Today's Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
        val upcomingPayment = paymentDueToday.copy(
            id = UUID.randomUUID(),
            title = "Upcoming Payment",
            date = paymentDueToday.date.plusDays(1)
        )

        val items = listOf(
            PaymentListItem.StaticHeader(R.string.overdue_payments),
            PaymentListItem.PaymentEntry(overduePayment),
            PaymentListItem.StaticHeader(R.string.payments_due_today),
            PaymentListItem.PaymentEntry(paymentDueToday),
            PaymentListItem.StaticHeader(R.string.upcoming_payments),
            PaymentListItem.PaymentEntry(upcomingPayment)
        )

        composeTestRule.setContent {
            PaymentListContent(paymentListItems = items)
        }

        composeTestRule.onNodeWithText(overduePayment.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(paymentDueToday.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(upcomingPayment.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("Overdue payments").assertIsDisplayed()
        composeTestRule.onNodeWithText("Payments due today").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upcoming payments").assertIsDisplayed()
    }

    @Test
    fun paymentItem_expand_showsActions() {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
        val items = listOf(PaymentListItem.PaymentEntry(payment))

        composeTestRule.setContent {
            PaymentListContent(paymentListItems = items)
        }

        composeTestRule.onNodeWithText(payment.title).performClick()
        composeTestRule.onNodeWithText("Complete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test
    fun paymentItem_expand_showsActionsOnlyOneAtATime() {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Payment 1",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
        val payment2 = payment.copy(id = UUID.randomUUID(), title = "Payment 2")

        composeTestRule.setContent {
            PaymentListContent(
                listOf(
                    PaymentListItem.PaymentEntry(payment), PaymentListItem.PaymentEntry(payment2)
                )
            )
        }

        composeTestRule.onNodeWithText("Payment 1").performClick()
        composeTestRule.onAllNodesWithText("Complete").assertCountEquals(1)
        composeTestRule.onNodeWithText("Payment 2").performClick()
        composeTestRule.onAllNodesWithText("Complete").assertCountEquals(1)
    }

    @Test
    fun paymentItem_editButton_callsOnEditClick() {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now().plusDays(1),
            frequency = PaymentFrequency.MONTHLY
        )
        var editCalled = false
        val items = listOf(PaymentListItem.PaymentEntry(payment))

        composeTestRule.setContent {
            PaymentListContent(paymentListItems = items, onEditClick = { editCalled = true })
        }

        composeTestRule.onNodeWithText(payment.title).performClick()
        composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edit").performClick()

        assertTrue(editCalled)
    }

    @Test
    fun paymentItem_deleteButton_callsOnDeleteClick() {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now().plusDays(1),
            frequency = PaymentFrequency.MONTHLY
        )
        var deleteClicked = false

        composeTestRule.setContent {
            PaymentListContent(listOf(PaymentListItem.PaymentEntry(payment)),
                onDeleteClick = { deleteClicked = true })
        }

        composeTestRule.onNodeWithText(payment.title).performClick()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").performClick()

        assertTrue(deleteClicked)
    }

    @Test
    fun paymentItem_completeButton_callsOnCompleteClick() {
        val payment = Payment(
            id = UUID.randomUUID(),
            title = "Test Payment",
            amount = Money.of(CurrencyUnit.GBP, 10.00),
            date = LocalDateTime.now(),
            frequency = PaymentFrequency.MONTHLY
        )
        var completeClicked = false

        composeTestRule.setContent {
            PaymentListContent(paymentListItems = listOf(PaymentListItem.PaymentEntry(payment)),
                onCompleteClick = { completeClicked = true })
        }

        composeTestRule.onNodeWithText(payment.title).performClick()
        composeTestRule.onNodeWithText("Complete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Complete").performClick()

        assertTrue(completeClicked)
    }

    @Test
    fun fabClick_callsOnAddClick() {
        var fabClicked = false

        composeTestRule.setContent {
            PaymentListScreenContent(
                state = PaymentUiState.Success(listOf()),
                onAddClick = { fabClicked = true },
                onHistoryClick = {},
                onErrorDialogDismiss = {},
                onDeleteClick = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Add Payment").performClick()
        assertTrue(fabClicked)
    }

    @Test
    fun historyIconClick_callsOnHistoryClick() {
        var historyIconClicked = false

        composeTestRule.setContent {
            PaymentListScreenContent(state = PaymentUiState.Success(listOf()),
                onHistoryClick = { historyIconClicked = true },
                onAddClick = {},
                onErrorDialogDismiss = {},
                onDeleteClick = {})
        }

        composeTestRule.onNodeWithContentDescription("Open Payment History").performClick()
        assertTrue(historyIconClicked)
    }

    @Test
    fun paymentListContent_emptyList_showsNoItems() {
        composeTestRule.setContent {
            PaymentListContent(
                paymentListItems = listOf()
            )
        }
        composeTestRule.onNodeWithText("Overdue payments").assertDoesNotExist()
        composeTestRule.onNodeWithText("Payments due today").assertDoesNotExist()
        composeTestRule.onNodeWithText("Upcoming payments").assertDoesNotExist()
    }

    @Test
    fun paymentListScreenContent_loading_showsLoadingScreen() {
        composeTestRule.setContent {
            PaymentListScreenContent(state = PaymentUiState.Loading,
                onAddClick = {},
                onHistoryClick = {},
                onErrorDialogDismiss = {},
                onDeleteClick = {})
        }

        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }

    @Test
    fun paymentListScreenContent_error_showsErrorScreen() {
        composeTestRule.setContent {
            PaymentListScreenContent(state = PaymentUiState.Error(Throwable("error")),
                onAddClick = {},
                onHistoryClick = {},
                onErrorDialogDismiss = {},
                onDeleteClick = {})
        }

        composeTestRule.onNodeWithText("There was an error loading data.\nPlease try again later.")
            .assertIsDisplayed()
    }
}
