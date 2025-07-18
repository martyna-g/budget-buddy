package pl.tinks.budgetbuddy.payment.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.PaymentListItem
import pl.tinks.budgetbuddy.payment.PaymentListScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    paymentListItems: List<PaymentListItem>,
    onInfoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.payment_history)) }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
    }) { innerPadding ->
        PaymentListScreenContent(
            paymentListItems = paymentListItems,
            onInfoClick = onInfoClick,
            onDeleteClick = onDeleteClick,
            modifier = modifier.padding(innerPadding),
        )
    }
}
