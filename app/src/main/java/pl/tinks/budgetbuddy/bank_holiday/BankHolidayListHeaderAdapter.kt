package pl.tinks.budgetbuddy.bank_holiday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemBankHolidayHeaderBinding

class BankHolidayListHeaderAdapter(private val headerTitle: String) :
    RecyclerView.Adapter<BankHolidayListHeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(val binding: ItemBankHolidayHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemBankHolidayHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.binding.layoutBankHolidayHeader.textHeader.text = headerTitle
    }

    override fun getItemCount(): Int = 1
}
