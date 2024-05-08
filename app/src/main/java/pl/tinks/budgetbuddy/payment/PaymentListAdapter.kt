package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class PaymentListAdapter(
    private val actionButtonClickCallback: (Int, UUID) -> Unit,
    private val actionMoveToHistoryButtonClickCallback: (Payment) -> Unit,
) :
    ListAdapter<Payment, PaymentListAdapter.PaymentListViewHolder>(PaymentDiffCallback()) {

    private var lastClickedPayment: Payment? = null
    private var lastClickedPosition: Int = RecyclerView.NO_POSITION

    inner class PaymentListViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val alphaAnimationIn = AlphaAnimation(0f, 1f)
        private val alphaAnimationOut = AlphaAnimation(1f, 0f)

        init {
            alphaAnimationIn.duration = 500
            alphaAnimationIn.interpolator = AccelerateDecelerateInterpolator()
            alphaAnimationOut.duration = 70
            alphaAnimationOut.interpolator = AccelerateInterpolator()
        }

        fun bind(payment: Payment) {

            val date = ZonedDateTime.of(payment.date, ZoneId.of("UTC"))
            val actionButtonsLayout = binding.layoutItemActionButtons
            val actionButtonsLayoutPaymentsDue = binding.layoutItemActionButtonsPaymentDue

            val today = LocalDateTime.now().toLocalDate()
            val isUpcoming = payment.date.toLocalDate() > today
            val isDue = payment.date.toLocalDate() <= today

            actionButtonsLayout.visibility = View.GONE
            actionButtonsLayoutPaymentsDue.visibility = View.GONE

            if (lastClickedPayment == payment) {
                if (isUpcoming) {
                    actionButtonsLayout.visibility = View.VISIBLE
                } else if (isDue) {
                    actionButtonsLayoutPaymentsDue.visibility = View.VISIBLE
                }
            }

            with(binding) {
                textPaymentTitle.text = payment.title
                textPaymentAmount.text = payment.amount.toString()
                textPaymentDateDay.text = date.dayOfMonth.toString()
                textPaymentDateMonth.text = date.month.toString().substring(0..2)
                root.setOnClickListener {

                    if (lastClickedPosition != bindingAdapterPosition && lastClickedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(lastClickedPosition)
                    }

                    if (isUpcoming) {
                        toggleLayoutVisibility(actionButtonsLayout, payment)
                    } else if (isDue) {
                        toggleLayoutVisibility(actionButtonsLayoutPaymentsDue, payment)
                    }

                    lastClickedPosition = bindingAdapterPosition

                }

                buttonItemMoveToHistory.setOnClickListener {
                    actionMoveToHistoryButtonClickCallback(payment)
                }

                setClickListener(buttonItemInfo, buttonItemInfo.id, payment.id)
                setClickListener(buttonItemInfoPaymentDue, buttonItemInfo.id, payment.id)
                setClickListener(buttonItemEdit, buttonItemEdit.id, payment.id)
                setClickListener(buttonItemDelete, buttonItemDelete.id, payment.id)
            }
        }

        private fun setClickListener(button: View, buttonId: Int, paymentId: UUID) {
            button.setOnClickListener {
                actionButtonClickCallback(buttonId, paymentId)
                lastClickedPayment = null
                notifyItemChanged(bindingAdapterPosition)
            }
        }

        private fun toggleLayoutVisibility(layout: View, payment: Payment) {
            if (layout.visibility == View.VISIBLE) {
                layout.startAnimation(alphaAnimationOut)
                alphaAnimationOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        layout.visibility = View.GONE
                        lastClickedPayment = null
                    }
                })
            } else {
                layout.startAnimation(alphaAnimationIn)
                layout.visibility = View.VISIBLE
                lastClickedPayment = payment
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
