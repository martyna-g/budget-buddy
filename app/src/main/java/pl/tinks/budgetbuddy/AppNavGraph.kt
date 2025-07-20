package pl.tinks.budgetbuddy

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.tinks.budgetbuddy.payment.history.PaymentHistoryScreen
import pl.tinks.budgetbuddy.payment.list.PaymentDetailsScreen
import pl.tinks.budgetbuddy.payment.list.PaymentListScreen
import java.util.UUID

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.PaymentList
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(Routes.PaymentList) {
            PaymentListScreen(
                viewModel = viewModel(),
                navController = navController,
            )
        }
        composable(Routes.PaymentHistory) {
            PaymentHistoryScreen(
                viewModel = viewModel(),
                navController = navController,
            )
        }
        composable(Routes.PaymentDetails) { backStackEntry ->
            val paymentId =
                backStackEntry.arguments?.getString(Routes.PaymentId)?.let { UUID.fromString(it) }
            PaymentDetailsScreen(
                viewModel = viewModel(),
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
