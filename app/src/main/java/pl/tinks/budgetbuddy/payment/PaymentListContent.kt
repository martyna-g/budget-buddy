package pl.tinks.budgetbuddy.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.tinks.budgetbuddy.SectionHeader
import java.util.UUID

@Composable
fun PaymentListContent(
    paymentListItems: List<PaymentListItem>,
    onDeleteClick: (Payment) -> Unit = {},
    onEditClick: (Payment) -> Unit = {},
    onCompleteClick: (Payment) -> Unit = {},
    onUndoCompleteClick: (Payment) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expandedPaymentId by remember { mutableStateOf<UUID?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(paymentListItems, key = { item ->
            when (item) {
                is PaymentListItem.StaticHeader -> "static_header_${item.resId}"
                is PaymentListItem.DynamicHeader -> "dynamic_header_${item.headerText}"
                is PaymentListItem.PaymentEntry -> item.payment.id
            }
        }) { item ->
            when (item) {
                is PaymentListItem.StaticHeader -> SectionHeader(stringResource(item.resId))
                is PaymentListItem.DynamicHeader -> SectionHeader(item.headerText)
                is PaymentListItem.PaymentEntry -> PaymentRow(payment = item.payment,
                    isExpanded = expandedPaymentId == item.payment.id,
                    onExpandClick = {
                        expandedPaymentId =
                            if (expandedPaymentId == item.payment.id) null else item.payment.id
                    },
                    onEditClick = { onEditClick(item.payment) },
                    onDeleteClick = { onDeleteClick(item.payment) },
                    onCompleteClick = { onCompleteClick(item.payment) },
                    onUndoCompleteClick = { onUndoCompleteClick(item.payment) })
            }
        }
    }
}
