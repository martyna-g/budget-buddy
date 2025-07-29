package pl.tinks.budgetbuddy

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int,
) {
    data object Payments : BottomNavScreen(
        route = Routes.PaymentList, icon = Icons.AutoMirrored.Outlined.ListAlt, labelRes = R.string.payments
    )

    data object Shopping : BottomNavScreen(
        route = Routes.ShoppingList, icon = Icons.Outlined.ShoppingCart, labelRes = R.string.shopping
    )

    data object BankHolidays : BottomNavScreen(
        route = Routes.BankHolidays, icon = Icons.Outlined.Event, labelRes = R.string.bank_holidays
    )

    companion object {
        val screens = listOf(Payments, Shopping, BankHolidays)
    }
}

fun isBottomNavScreen(route: String?): Boolean {
    return BottomNavScreen.screens.any { it.route == route }
}
