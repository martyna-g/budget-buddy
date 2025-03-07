package pl.tinks.budgetbuddy.payment.list

import pl.tinks.budgetbuddy.payment.PaymentFrequency
import java.time.LocalDate
import javax.inject.Inject

class PaymentDateCalculator @Inject constructor() {

    fun calculateNextPaymentDate(
        paymentDate: LocalDate, paymentFrequency: PaymentFrequency
    ): LocalDate? {
        val nextPaymentDate = when (paymentFrequency) {
            PaymentFrequency.SINGLE_PAYMENT -> paymentDate
            PaymentFrequency.DAILY -> paymentDate.plusDays(1)
            PaymentFrequency.WEEKLY -> paymentDate.plusDays(7)
            PaymentFrequency.MONTHLY -> paymentDate.plusMonths(1)
            PaymentFrequency.QUARTERLY -> paymentDate.plusMonths(4)
            PaymentFrequency.BIANNUALLY -> paymentDate.plusMonths(6)
            PaymentFrequency.ANNUALLY -> paymentDate.plusMonths(12)
        }

        return nextPaymentDate
    }
}
