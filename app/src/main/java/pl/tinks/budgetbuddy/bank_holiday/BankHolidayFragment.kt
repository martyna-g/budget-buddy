package pl.tinks.budgetbuddy.bank_holiday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentBankHolidayBinding

class BankHolidayFragment : Fragment() {

    private lateinit var binding: FragmentBankHolidayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentBankHolidayBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayoutBankHoliday
        val viewPager = binding.viewPagerBankHoliday
        val adapter = BankHolidayPagerAdapter(this, requireActivity())

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.region_england_and_wales)
                1 -> getString(R.string.region_scotland)
                2 -> getString(R.string.region_northern_ireland)
                else -> throw IllegalArgumentException(getString(R.string.invalid_position))
            }
        }.attach()

        ViewCompat.setOnApplyWindowInsetsListener(tabLayout) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val layoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBarsInsets.top
            v.layoutParams = layoutParams

            insets
        }

    }

}
