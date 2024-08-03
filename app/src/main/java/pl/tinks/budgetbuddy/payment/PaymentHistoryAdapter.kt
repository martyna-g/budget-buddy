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
    private val paymentItemLongClickCallback: (Payment) -> Unit,
    private val paymentItemClickCallback: (Payment) -> Unit,
    private val isSelected: (Payment) -> Boolean
) : ListAdapter<Payment, PaymentHistoryAdapter.PaymentHistoryViewHolder>(
    PaymentHistoryDiffCallback()
) {

    inner class PaymentHistoryViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            payment: Payment,
            isSelected: Boolean,
            paymentItemClickCallback: (Payment) -> Unit,
            paymentItemLongClickCallback: (Payment) -> Unit
        ) {
            val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
            val itemBackground =
                ContextCompat.getDrawable(itemView.context, R.drawable.item_view_background)
            val selectedItemBackground = ContextCompat.getDrawable(
                itemView.context, R.drawable.item_view_background_selected
            )
            itemView.background = if (isSelected) selectedItemBackground else itemBackground

            with(binding) {
                textPaymentTitle.text = payment.title
                textPaymentAmount.text = payment.amount.toString()
                textPaymentDateDay.text = date.dayOfMonth.toString()
                textPaymentDateMonth.text = date.month.toString().substring(0..2)
                root.setOnClickListener {
                    paymentItemClickCallback(payment)
                }
                root.setOnLongClickListener {
                    paymentItemLongClickCallback(payment)
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
        val payment = getItem(position)
        holder.bind(
            payment,
            isSelected(payment),
            paymentItemClickCallback,
            paymentItemLongClickCallback
        )
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
