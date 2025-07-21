package pl.tinks.budgetbuddy.payment.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.tinks.budgetbuddy.R
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailsScreen(
    viewModel: PaymentDetailsScreenViewModel,
    onCancel: () -> Unit,
    paymentId: UUID? = null,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onCancel()
        }
    }

    LaunchedEffect(paymentId) {
        if (paymentId != null) {
            viewModel.loadPayment(paymentId)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                if (state.isEdit) stringResource(R.string.edit_payment) else stringResource(
                    R.string.add_payment
                )
            )
        }, navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
            }
        }, actions = {
            IconButton(onClick = viewModel::onSavePayment) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            }
        })
    }) { innerPadding ->
        PaymentDetailsScreenContent(
            state = state,
            onTitleChange = viewModel::onTitleChange,
            onAmountChange = viewModel::onAmountChange,
            onDateChange = viewModel::onDateChange,
            onFrequencyChange = viewModel::onFrequencyChange,
            onNotificationChange = viewModel::onNotificationChange,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
