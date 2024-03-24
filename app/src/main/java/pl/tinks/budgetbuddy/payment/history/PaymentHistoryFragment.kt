package pl.tinks.budgetbuddy.payment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentPaymentHistoryBinding
import pl.tinks.budgetbuddy.payment.PaymentHistoryAdapter

@AndroidEntryPoint
class PaymentHistoryFragment : Fragment() {

    private val viewModel: PaymentHistoryViewModel by viewModels()
    private lateinit var binding: FragmentPaymentHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private val paymentHistoryAdapter = PaymentHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentHistoryBinding.inflate(inflater, container, false)

        toolbar = binding.toolbarPaymentHistory
        recyclerView = binding.recyclerviewPaymentHistory
        recyclerView.adapter = paymentHistoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PaymentHistoryUiState.Loading -> disableUserInteractions()
                        is PaymentHistoryUiState.Error -> showErrorDialog()
                        is PaymentHistoryUiState.Success -> {
                            enableUserInteractions()
                            paymentHistoryAdapter.submitList(uiState.data)
                        }
                    }
                }
            }
        }

    }

    private fun disableUserInteractions() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
    }

    private fun enableUserInteractions() {
        recyclerView.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity())
            .setMessage(R.string.payments_loading_error_message)
            .setPositiveButton(R.string.payments_loading_error_ok) { _, _ -> enableUserInteractions() }
            .show()
    }

}
