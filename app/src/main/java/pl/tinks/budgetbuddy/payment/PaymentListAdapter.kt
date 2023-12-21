package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding

class PaymentListAdapter : RecyclerView.Adapter<PaymentListAdapter.PaymentListViewHolder>() {

    class PaymentListViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
        val binding = ItemPaymentBinding
            .inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        return PaymentListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}