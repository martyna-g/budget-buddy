package pl.tinks.budgetbuddy.payment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.PaymentRepository
import javax.inject.Inject

@HiltViewModel
class AddPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    fun addPayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepository.addPayment(payment)
        }
    }

    fun getFrequencies(): Map<PaymentFrequency, String> {
        return mapOf(
            PaymentFrequency.SINGLE_PAYMENT to "Single payment",
            PaymentFrequency.DAILY to "Daily",
            PaymentFrequency.WEEKLY to "Weekly",
            PaymentFrequency.MONTHLY to "Monthly",
            PaymentFrequency.QUARTERLY to "Quarterly",
            PaymentFrequency.BIANNUALLY to "Biannually",
            PaymentFrequency.ANNUALLY to "Annually",
        )
    }

}
