package pl.tinks.budgetbuddy.payment.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.payment.PaymentListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentListScreen(
    paymentListItems: List<PaymentListItem>,
    onInfoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onMoveToHistoryClick: () -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }, actions = {
            IconButton(onClick = onHistoryClick) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.open_payment_history)
                )
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onFabClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_payment)
            )
        }
    }) { innerPadding ->
        PaymentListScreenContent(
            paymentListItems = paymentListItems,
            onInfoClick = onInfoClick,
            onDeleteClick = onDeleteClick,
            onEditClick = onEditClick,
            onMoveToHistoryClick = onMoveToHistoryClick,
            modifier = modifier.padding(innerPadding)
        )
    }
}
