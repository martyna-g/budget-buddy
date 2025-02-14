package pl.tinks.budgetbuddy

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.tinks.budgetbuddy.shopping.ShoppingDao
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingListAdapter
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class ShoppingListUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var database: BudgetBuddyDatabase

    @Inject
    lateinit var shoppingDao: ShoppingDao

    private lateinit var item: ShoppingItem

    @Before
    fun setUp() {
        hiltRule.inject()

        item = ShoppingItem(
            id = UUID.randomUUID(), itemName = "Test Item", inBasket = false
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun shoppingListScreen_opensSuccessfully() {
        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(isDisplayed()))
    }

    @Test
    fun addItem_displaysItemInList() {
        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.edit_text_shopping_item)).perform(click())

        onView(withId(R.id.edit_text_shopping_item)).perform(replaceText("Test Item"))

        onView(withId(R.id.button_add_new_item)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Test Item"))))
    }

    @Test
    fun clickUncheckedItem_marksItemAsChecked() = runTest {
        shoppingDao.addShoppingItem(item)

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.image_shopping_item_checked)).check(matches(not(isDisplayed())))

        onView(withText("Test Item")).perform(click())

        onView(withId(R.id.image_shopping_item_checked)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCheckedItem_marksItemAsUnchecked() = runTest {
        shoppingDao.addShoppingItem(item.copy(inBasket = true))

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.image_shopping_item_checked)).check(matches(isDisplayed()))

        onView(withText("Test Item")).perform(click())

        onView(withId(R.id.image_shopping_item_checked)).check(matches(not(isDisplayed())))
    }

    @Test
    fun deleteSelectedItems_deletesSelectedItemsFromList() = runTest {
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Item to delete", inBasket = true
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Item to delete 2", inBasket = false
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Item to keep", inBasket = false
            )
        )

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Item to delete"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Item to delete 2"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Item to keep"))))

        onView(withText("Item to delete")).perform(longClick())

        onView(withText("Item to delete 2")).perform(click())

        onView(withId(R.id.action_delete_selected_shopping_items)).perform(click())

        onView(withText("Yes")).check(matches(isDisplayed()))

        onView(withText("Yes")).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Item to delete")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Item to delete 2")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Item to keep"))))
    }

    @Test
    fun deleteCheckedItems_deletesCheckedItemsFromList() = runTest {
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Checked Item", inBasket = true
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Checked Item 2", inBasket = true
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Unchecked Item", inBasket = false
            )
        )

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Checked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Checked Item 2"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Unchecked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ShoppingListAdapter.ShoppingListViewHolder>(
                0, longClick()
            )
        )

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Delete checked items")).perform(click())

        onView(withText("Yes")).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Checked Item")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Checked Item 2")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Unchecked Item"))))
    }

    @Test
    fun deleteUncheckedItems_deletesUncheckedItemsFromList() = runTest {
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Checked Item", inBasket = true
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Unchecked Item", inBasket = false
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Unchecked Item 2", inBasket = false
            )
        )

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Checked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Unchecked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Unchecked Item 2"))))

        onView(withId(R.id.recyclerview_shopping_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ShoppingListAdapter.ShoppingListViewHolder>(
                0, longClick()
            )
        )

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Delete unchecked items")).perform(click())

        onView(withText("Yes")).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Unchecked Item")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Unchecked Item 2")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Checked Item"))))
    }

    @Test
    fun deleteAllItems_deletesAllItemsFromList() = runTest {
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Checked Item", inBasket = true
            )
        )
        shoppingDao.addShoppingItem(
            ShoppingItem(
                UUID.randomUUID(), "Unchecked Item", inBasket = false
            )
        )

        onView(withId(R.id.shopping_list_fragment)).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Checked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(hasDescendant(withText("Unchecked Item"))))

        onView(withId(R.id.recyclerview_shopping_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ShoppingListAdapter.ShoppingListViewHolder>(
                0, longClick()
            )
        )

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Delete all items")).perform(click())

        onView(withText("Yes")).perform(click())

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Checked Item")))))

        onView(withId(R.id.recyclerview_shopping_list)).check(matches(not(hasDescendant(withText("Unchecked Item")))))
    }
}
