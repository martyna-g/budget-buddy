package pl.tinks.budgetbuddy.payment

sealed class PaymentItem {
    data class Header(val title: String) : PaymentItem()
    data class PaymentEntry(val payment: Payment) : PaymentItem()
}
