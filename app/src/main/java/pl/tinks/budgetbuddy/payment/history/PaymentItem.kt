package pl.tinks.budgetbuddy.payment.history

import pl.tinks.budgetbuddy.payment.Payment

sealed class PaymentItem {
    data class Header(val title: String) : PaymentItem()
    data class PaymentEntry(val payment: Payment) : PaymentItem()
}
