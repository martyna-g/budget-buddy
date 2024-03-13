package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import java.time.ZoneId
import java.time.ZonedDateTime

class PaymentHistoryAdapter : ListAdapter<Payment, PaymentHistoryAdapter.PaymentHistoryViewHolder>(
    PaymentHistoryDiffCallback()
) {

    class PaymentHistoryViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
            with(binding) {
                textPaymentTitle.text = payment.title
                textPaymentAmount.text = payment.amount.toString()
                textPaymentDateDay.text = date.dayOfMonth.toString()
                textPaymentDateMonth.text = date.month.toString().substring(0..2)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentHistoryDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }

}
