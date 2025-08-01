package pl.tinks.budgetbuddy

import java.util.UUID

object Routes {
    const val PaymentId = "paymentId"
    const val PaymentList = "payment_list"
    const val ShoppingList = "shopping_list"
    const val BankHolidays = "bank_holidays"
    const val PaymentHistory = "payment_history"
    const val PaymentDetails = "payment_details"
    const val PaymentDetailsWithId = "payment_details/{$PaymentId}"

    fun paymentDetailsWithId(id: UUID) = "payment_details/$id"
}
