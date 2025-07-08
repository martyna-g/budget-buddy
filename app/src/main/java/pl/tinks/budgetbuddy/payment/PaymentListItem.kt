package pl.tinks.budgetbuddy.payment

sealed class PaymentListItem {
    data class Header(val title: String) : PaymentListItem()
    data class PaymentEntry(val payment: Payment) : PaymentListItem()
}
