package pl.tinks.budgetbuddy.payment

sealed class PaymentListItem {
    data class StaticHeader(val resId: Int) : PaymentListItem()
    data class DynamicHeader(val headerText: String) : PaymentListItem()
    data class PaymentEntry(val payment: Payment) : PaymentListItem()
}
