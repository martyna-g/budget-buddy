package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemHeaderBinding
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import pl.tinks.budgetbuddy.payment.history.PaymentItem
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class PaymentHistoryAdapter(
    private val paymentItemLongClickCallback: (Payment) -> Unit,
    private val paymentItemClickCallback: (Payment) -> Unit,
    private val isSelected: (Payment) -> Boolean
) : ListAdapter<PaymentItem, PaymentHistoryAdapter.PaymentHistoryViewHolder>(PaymentItemDiffCallback()) {

    sealed class PaymentHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class PaymentViewHolder(val binding: ItemPaymentBinding) : PaymentHistoryViewHolder(binding.root) {
            fun bind(
                payment: Payment,
                isSelected: Boolean,
                paymentItemClickCallback: (Payment) -> Unit,
                paymentItemLongClickCallback: (Payment) -> Unit
            ) {
                val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
                val itemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.item_view_background)
                val selectedItemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.item_view_background_selected)
                itemView.background = if (isSelected) selectedItemBackground else itemBackground

                with(binding) {
                    textPaymentTitle.text = payment.title
                    textPaymentAmount.text = payment.amount.toString()
                    textPaymentDateDay.text =
                        String.format(Locale.getDefault(), "%d", date.dayOfMonth)
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

        class HeaderViewHolder(val binding: ItemHeaderBinding) : PaymentHistoryViewHolder(binding.root) {
            fun bind(title: String) {
                binding.textHeader.text = title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        return when (viewType) {
            R.layout.item_payment -> {
                val binding = ItemPaymentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PaymentHistoryViewHolder.PaymentViewHolder(binding)
            }
            R.layout.item_header -> {
                val binding = ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PaymentHistoryViewHolder.HeaderViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        when (holder) {
            is PaymentHistoryViewHolder.PaymentViewHolder -> {
                val paymentEntry = getItem(position) as PaymentItem.PaymentEntry
                holder.bind(
                    paymentEntry.payment,
                    isSelected(paymentEntry.payment),
                    paymentItemClickCallback,
                    paymentItemLongClickCallback
                )
            }
            is PaymentHistoryViewHolder.HeaderViewHolder -> holder.bind(
                (getItem(position) as PaymentItem.Header).title
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PaymentItem.Header -> R.layout.item_header
            is PaymentItem.PaymentEntry -> R.layout.item_payment
        }
    }

    class PaymentItemDiffCallback : DiffUtil.ItemCallback<PaymentItem>() {
        override fun areItemsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
            return if (oldItem is PaymentItem.Header && newItem is PaymentItem.Header) {
                oldItem.title == newItem.title
            } else if (oldItem is PaymentItem.PaymentEntry && newItem is PaymentItem.PaymentEntry) {
                oldItem.payment.id == newItem.payment.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
            return oldItem == newItem
        }
    }
}
