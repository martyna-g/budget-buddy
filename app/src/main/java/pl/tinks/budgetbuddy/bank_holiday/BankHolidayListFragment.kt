package pl.tinks.budgetbuddy.bank_holiday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentBankHolidayListBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class BankHolidayListFragment : Fragment() {

    private lateinit var binding: FragmentBankHolidayListBinding
    private val viewModel: BankHolidayViewModel by viewModels()
    private lateinit var region: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            region = it.getString(ARG_REGION) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBankHolidayListBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewBankHoliday.apply {
            layoutManager = LinearLayoutManager(requireActivity())
        }
        progressIndicator = binding.progressIndicatorBankHoliday

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collectLatest { uiState ->
                    progressIndicator.isVisible = uiState is BankHolidayUiState.Loading
                    when (uiState) {
                        is BankHolidayUiState.Success -> updateUi(uiState.bankHolidays)
                        is BankHolidayUiState.Error -> showErrorDialog()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun updateUi(bankHolidays: List<BankHoliday>) {
        val filteredBankHolidays = bankHolidays.filter { it.region == region }
        val nextBankHoliday = filteredBankHolidays.firstOrNull()

        nextBankHoliday?.let {
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM")
            val formattedDate = it.date.format(outputFormatter)
            binding.textViewNextBankHolidayDate.text = formattedDate.toString()
            binding.textViewNextBankHolidayTitle.text = it.title
            binding.textViewNextBankHoliday.text =
                getString(R.string.next_bank_holiday_text, region)
            binding.textViewUpcomingBankHolidays.text =
                getString(R.string.upcoming_bank_holidays_text, region)
            binding.layoutNextBankHoliday.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.item_view_background)
        }
        setupConcatAdapter(filteredBankHolidays)
    }

    private fun setupConcatAdapter(bankHolidays: List<BankHoliday>) {
        val concatAdapter = ConcatAdapter()
        val groupedByYear = bankHolidays.groupBy { it.date.year }

        groupedByYear.forEach { (year, bankHolidaysInYear) ->
            val headerAdapter = BankHolidayListHeaderAdapter(year.toString())
            val bankHolidayListAdapter =
                BankHolidayListAdapter().apply { submitList(bankHolidaysInYear) }

            concatAdapter.addAdapter(headerAdapter)
            concatAdapter.addAdapter(bankHolidayListAdapter)
        }

        recyclerView.adapter = concatAdapter
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity())
            .setMessage(R.string.bank_holiday_loading_error_message)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                progressIndicator.isVisible = false
            }.show()
    }

    companion object {
        private const val ARG_REGION = "region"

        fun newInstance(region: String) = BankHolidayListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_REGION, region)
            }
        }
    }
}
