package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import java.time.ZoneId
import java.time.ZonedDateTime

class PaymentHistoryAdapter(
    private val paymentItemLongClickCallback: () -> Unit,
    private val emptyListCallback: () -> Unit,
) : ListAdapter<Payment, PaymentHistoryAdapter.PaymentHistoryViewHolder>(
    PaymentHistoryDiffCallback()
) {

    val selectedPayments: MutableList<Payment> = mutableListOf()

    inner class PaymentHistoryViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))

            val itemBackground =
                ContextCompat.getDrawable(itemView.context, R.drawable.item_view_background)
            val selectedItemBackground = ContextCompat.getDrawable(
                itemView.context, R.drawable.item_view_background_selected
            )
            itemView.background = itemBackground

            with(binding) {
                textPaymentTitle.text = payment.title
                textPaymentAmount.text = payment.amount.toString()
                textPaymentDateDay.text = date.dayOfMonth.toString()
                textPaymentDateMonth.text = date.month.toString().substring(0..2)
                root.setOnClickListener {
                    if (selectedPayments.isNotEmpty()) {
                        if (selectedPayments.contains(payment)) {
                            selectedPayments.remove(payment)
                            root.background = itemBackground
                            if (selectedPayments.isEmpty()) emptyListCallback()
                        } else {
                            selectedPayments.add(payment)
                            root.background = selectedItemBackground
                        }
                    }
                }
                root.setOnLongClickListener {
                    if (selectedPayments.isEmpty()) {
                        selectedPayments.add(payment)
                        root.background = selectedItemBackground
                        paymentItemLongClickCallback()
                    }
                    true
                }
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
