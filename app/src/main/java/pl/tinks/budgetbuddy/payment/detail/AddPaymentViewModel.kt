package pl.tinks.budgetbuddy.payment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentRepository

class AddPaymentViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {

    fun addPayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepository.addPayment(payment)
        }
    }

}
