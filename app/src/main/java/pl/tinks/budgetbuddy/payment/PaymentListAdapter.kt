package pl.tinks.budgetbuddy.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.ItemPaymentBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class PaymentListAdapter(private val actionButtonClickCallback: (Int, UUID) -> Unit) :
    ListAdapter<Payment, PaymentListAdapter.PaymentListViewHolder>(PaymentDiffCallback()) {

    private var lastClickedPosition: Int = RecyclerView.NO_POSITION

    inner class PaymentListViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val alphaAnimationIn = AlphaAnimation(0f, 1f)
        private val alphaAnimationOut = AlphaAnimation(1f, 0f)

        init {
            alphaAnimationIn.duration = 500
            alphaAnimationIn.interpolator = AccelerateDecelerateInterpolator()
            alphaAnimationOut.duration = 170
            alphaAnimationOut.interpolator = AccelerateInterpolator()
        }

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

                    if (actionButtonsLayout.visibility == View.VISIBLE) {
                        actionButtonsLayout.startAnimation(alphaAnimationOut)
                        alphaAnimationOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}
                            override fun onAnimationRepeat(animation: Animation?) {}
                            override fun onAnimationEnd(animation: Animation?) {
                                actionButtonsLayout.visibility = View.GONE
                            }
                        })
                    } else {
                        actionButtonsLayout.startAnimation(alphaAnimationIn)
                        actionButtonsLayout.visibility = View.VISIBLE
                    }

                    lastClickedPosition = adapterPosition

                }

                buttonItemInfo.setOnClickListener {
                    actionButtonClickCallback(buttonItemInfo.id, payment.id)
                }
                buttonItemEdit.setOnClickListener {
                    actionButtonClickCallback(buttonItemEdit.id, payment.id)
                }
                buttonItemDelete.setOnClickListener {
                    actionButtonClickCallback(buttonItemDelete.id, payment.id)
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
