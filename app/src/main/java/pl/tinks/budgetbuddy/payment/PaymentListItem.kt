package pl.tinks.budgetbuddy.payment

sealed class PaymentListItem {
    data class Header(val resId: Int) : PaymentListItem()
    data class PaymentEntry(val payment: Payment) : PaymentListItem()
}
