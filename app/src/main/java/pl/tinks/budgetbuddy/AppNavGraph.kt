package pl.tinks.budgetbuddy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pl.tinks.budgetbuddy.bankholiday.BankHolidayTabs
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryScreen
import pl.tinks.budgetbuddy.payment.list.PaymentDetailsScreen
import pl.tinks.budgetbuddy.payment.list.PaymentListScreen
import pl.tinks.budgetbuddy.shopping.ShoppingListScreen
import java.util.UUID

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = BottomNavScreen.Payments.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(BottomNavScreen.Payments.route) {
            PaymentListScreen(viewModel = hiltViewModel(),
                onAddClick = { navController.navigate(Routes.PaymentDetails) },
                onEditClick = { paymentId ->
                    navController.navigate(
                        Routes.paymentDetailsWithId(
                            paymentId
                        )
                    )
                },
                onHistoryClick = { navController.navigate(Routes.PaymentHistory) },
                onErrorDialogDismiss = { navController.popBackStack() })
        }
        composable(BottomNavScreen.Shopping.route) {
            ShoppingListScreen(viewModel = hiltViewModel(),
                onErrorDialogDismiss = { navController.popBackStack() })
        }
        composable(BottomNavScreen.BankHolidays.route) {
            BankHolidayTabs(viewModel = hiltViewModel(),
                onErrorDialogDismiss = { navController.popBackStack() })
        }
        composable(Routes.PaymentHistory) {
            PaymentHistoryScreen(viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onErrorDialogDismiss = { navController.popBackStack() })
        }
        composable(Routes.PaymentDetails) {
            PaymentDetailsScreen(
                viewModel = hiltViewModel(),
                onCancel = { navController.popBackStack() })
        }
        composable(Routes.PaymentDetailsWithId) { backStackEntry ->
            val paymentId =
                backStackEntry.arguments?.getString(Routes.PaymentId)?.let { UUID.fromString(it) }
            PaymentDetailsScreen(
                viewModel = hiltViewModel(),
                onCancel = { navController.popBackStack() },
                paymentId = paymentId
            )
        }
    }
}
