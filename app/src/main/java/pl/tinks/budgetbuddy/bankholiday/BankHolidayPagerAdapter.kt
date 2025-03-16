package pl.tinks.budgetbuddy.bankholiday

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.tinks.budgetbuddy.R

class BankHolidayPagerAdapter(
    fragment: Fragment,
    private val context: Context
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val region = when (position) {
            0 -> context.getString(R.string.region_england_and_wales)
            1 -> context.getString(R.string.region_scotland)
            2 -> context.getString(R.string.region_northern_ireland)
            else -> throw IllegalArgumentException(context.getString(R.string.invalid_position))
        }
        return BankHolidayListFragment.newInstance(region)
    }
}
