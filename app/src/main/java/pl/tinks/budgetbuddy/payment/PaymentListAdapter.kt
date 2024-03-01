package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class PaymentListAdapter @Inject constructor() :
    ListAdapter<Payment, PaymentListAdapter.PaymentListViewHolder>(PaymentDiffCallback()) {

    private var lastClickedPosition: Int = RecyclerView.NO_POSITION

    inner class PaymentListViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
            with(binding) {
                textPaymentTitle.text = payment.title
                textPaymentAmount.text = payment.amount.toString()
                textPaymentDateDay.text = date.dayOfMonth.toString()
                textPaymentDateMonth.text = date.month.toString().substring(0..2)
                root.setOnClickListener {
                    val actionButtonsLayout =
                        it.findViewById<ConstraintLayout>(R.id.layout_item_action_buttons)
                    if (lastClickedPosition != adapterPosition && lastClickedPosition != RecyclerView.NO_POSITION) {

                        val lastClickedView =
                            (it.parent as RecyclerView).findViewHolderForAdapterPosition(
                                lastClickedPosition
                            )?.itemView

                        lastClickedView?.findViewById<ConstraintLayout>(R.id.layout_item_action_buttons)?.visibility =
                            View.GONE
                    }

                    actionButtonsLayout.visibility =
                        if (actionButtonsLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                    lastClickedPosition = adapterPosition

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaymentListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }

}
