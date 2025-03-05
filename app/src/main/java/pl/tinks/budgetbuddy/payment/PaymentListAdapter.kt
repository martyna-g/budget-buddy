package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemHeaderBinding
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import pl.tinks.budgetbuddy.payment.history.PaymentItem
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import java.util.UUID

class PaymentListAdapter(
    private val actionButtonClickCallback: (Int, UUID) -> Unit,
    private val actionMoveToHistoryButtonClickCallback: (Payment) -> Unit
) : ListAdapter<PaymentItem, PaymentListAdapter.PaymentListViewHolder>(PaymentItemDiffCallback()) {

    private var selectedPayment: Payment? = null
    private var recyclerView: RecyclerView? = null

    sealed class PaymentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class PaymentViewHolder(private val binding: ItemPaymentBinding) :
            PaymentListViewHolder(binding.root) {
            fun bind(
                payment: Payment,
                actionButtonClickCallback: (Int, UUID) -> Unit,
                actionMoveToHistoryButtonClickCallback: (Payment) -> Unit,
                isSelected: Boolean,
                onPaymentClick: (Payment) -> Unit
            ) {
                val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
                val actionButtonsLayout = binding.layoutItemActionButtons
                val actionButtonsLayoutPaymentsDue = binding.layoutItemActionButtonsPaymentDue

                val today = LocalDateTime.now().toLocalDate()
                val isUpcoming = payment.date.toLocalDate() > today
                val isDue = payment.date.toLocalDate() <= today

                actionButtonsLayout.visibility =
                    if (isSelected && isUpcoming) View.VISIBLE else View.GONE
                actionButtonsLayoutPaymentsDue.visibility =
                    if (isSelected && isDue) View.VISIBLE else View.GONE

                with(binding) {
                    textPaymentTitle.text = payment.title
                    textPaymentAmount.text = payment.amount.toString()
                    textPaymentDateDay.text =
                        String.format(Locale.getDefault(), "%d", date.dayOfMonth)
                    textPaymentDateMonth.text = date.month.toString().substring(0..2)
                    root.setOnClickListener {
                        onPaymentClick(payment)
                    }

                    buttonItemMoveToHistory.setOnClickListener {
                        actionMoveToHistoryButtonClickCallback(payment)
                    }

                    setClickListener(
                        buttonItemInfo, buttonItemInfo.id, payment.id, actionButtonClickCallback
                    )
                    setClickListener(
                        buttonItemInfoPaymentDue,
                        buttonItemInfo.id,
                        payment.id,
                        actionButtonClickCallback
                    )
                    setClickListener(
                        buttonItemEdit, buttonItemEdit.id, payment.id, actionButtonClickCallback
                    )
                    setClickListener(
                        buttonItemDelete, buttonItemDelete.id, payment.id, actionButtonClickCallback
                    )
                }
            }

            private fun setClickListener(
                button: View,
                buttonId: Int,
                paymentId: UUID,
                actionButtonClickCallback: (Int, UUID) -> Unit
            ) {
                button.setOnClickListener {
                    actionButtonClickCallback(buttonId, paymentId)
                }
            }
        }

        class HeaderViewHolder(val binding: ItemHeaderBinding) :
            PaymentListViewHolder(binding.root) {
            fun bind(title: String) {
                binding.textHeader.text = title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
        return when (viewType) {
            R.layout.item_payment -> {
                val binding =
                    ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PaymentListViewHolder.PaymentViewHolder(binding)
            }

            R.layout.item_header -> {
                val binding =
                    ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PaymentListViewHolder.HeaderViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
        when (holder) {
            is PaymentListViewHolder.PaymentViewHolder -> {
                val paymentEntry = getItem(position) as PaymentItem.PaymentEntry
                holder.bind(
                    paymentEntry.payment,
                    actionButtonClickCallback,
                    actionMoveToHistoryButtonClickCallback,
                    paymentEntry.payment == selectedPayment,
                    ::onPaymentClicked
                )
            }

            is PaymentListViewHolder.HeaderViewHolder -> holder.bind((getItem(position) as PaymentItem.Header).title)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PaymentItem.Header -> R.layout.item_header
            is PaymentItem.PaymentEntry -> R.layout.item_payment
        }
    }

    private fun onPaymentClicked(payment: Payment) {
        val previousSelectedPayment = selectedPayment
        selectedPayment = if (previousSelectedPayment == payment) null else payment

        val previousIndex = currentList.indexOfFirst {
            it is PaymentItem.PaymentEntry && it.payment == previousSelectedPayment
        }
        val newIndex = currentList.indexOfFirst {
            it is PaymentItem.PaymentEntry && it.payment == selectedPayment
        }

        notifyItemChanged(previousIndex)
        notifyItemChanged(newIndex)

        if (selectedPayment != null && newIndex != RecyclerView.NO_POSITION) {
            recyclerView?.post {
                recyclerView?.smoothScrollToPosition(newIndex)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
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
