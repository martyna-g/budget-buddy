package pl.tinks.budgetbuddy.bank_holiday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.tinks.budgetbuddy.databinding.ItemHeaderBinding

class BankHolidayListHeaderAdapter(private val headerTitle: String) :
    RecyclerView.Adapter<BankHolidayListHeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.binding.textHeader.text = headerTitle
    }

    override fun getItemCount(): Int = 1
}
