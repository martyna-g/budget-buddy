package pl.tinks.budgetbuddy.bank_holiday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemBankHolidayBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class BankHolidayListAdapter :
    ListAdapter<BankHoliday, BankHolidayListAdapter.BankHolidayViewHolder>(BankHolidayDiffCallback()) {

    class BankHolidayViewHolder(private val binding: ItemBankHolidayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bankHoliday: BankHoliday) {
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM")
            val formattedDate = bankHoliday.date.format(outputFormatter)

            val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
            val dayOfWeek = bankHoliday.date.format(dayOfWeekFormatter)

            binding.textViewItemBankHolidayDate.text = formattedDate.toString()
            binding.textViewItemBankHolidayDayOfWeek.text = dayOfWeek
            binding.textViewItemBankHolidayTitle.text = bankHoliday.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankHolidayViewHolder {
        val binding: ItemBankHolidayBinding = ItemBankHolidayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BankHolidayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankHolidayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BankHolidayDiffCallback : DiffUtil.ItemCallback<BankHoliday>() {
        override fun areItemsTheSame(
            oldItem: BankHoliday, newItem: BankHoliday
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: BankHoliday, newItem: BankHoliday
        ): Boolean {
            return oldItem == newItem
        }
    }
}
