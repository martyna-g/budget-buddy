package pl.tinks.budgetbuddy.payment.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentPaymentListBinding
import pl.tinks.budgetbuddy.payment.PaymentListAdapter
import pl.tinks.budgetbuddy.payment.detail.PaymentDetailsFragment
import java.util.UUID

@AndroidEntryPoint
class PaymentListFragment : Fragment() {

    private val viewModel: PaymentListViewModel by activityViewModels()
    private lateinit var binding: FragmentPaymentListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private val adapter: PaymentListAdapter = PaymentListAdapter(::handleActionButtonClick)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentListBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerviewPaymentList
        toolbar = binding.toolbarPaymentList
        floatingActionButton = binding.fabPaymentList
        progressIndicator = binding.progressIndicatorPaymentList

        val layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, layoutManager.orientation)

        recyclerView.also {
            it.adapter = adapter
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
        }

        floatingActionButton.setOnClickListener {
            navigateToPaymentDetailFragment(null, null)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PaymentUiState.Loading -> disableUserInteractions()
                        is PaymentUiState.Success -> {
                            enableUserInteractions()
                            adapter.submitList(uiState.data)
                        }

                        is PaymentUiState.Error -> showErrorDialog()
                    }
                }
            }
        }
    }

    private fun disableUserInteractions() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
        floatingActionButton.visibility = View.GONE
    }

    private fun enableUserInteractions() {
        progressIndicator.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        floatingActionButton.visibility = View.VISIBLE
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity())
            .setMessage(R.string.payments_loading_error_message)
            .setPositiveButton(R.string.payments_loading_error_ok) { _, _ -> enableUserInteractions() }
            .show()
    }

    private fun handleActionButtonClick(buttonId: Int, paymentId: UUID) {
        navigateToPaymentDetailFragment(buttonId, paymentId.toString())
    }

    private fun navigateToPaymentDetailFragment(buttonId: Int?, paymentId: String?) {
        val actionButtonType = when (buttonId) {
            R.id.button_item_info -> PaymentDetailsFragment.Companion.ActionButtonType.INFO
            R.id.button_item_edit -> PaymentDetailsFragment.Companion.ActionButtonType.EDIT
            R.id.button_item_delete -> PaymentDetailsFragment.Companion.ActionButtonType.DELETE
            else -> PaymentDetailsFragment.Companion.ActionButtonType.ADD
        }
        val action = PaymentListFragmentDirections
            .actionPaymentListFragmentToPaymentDetailsFragment(actionButtonType, paymentId)
        findNavController().navigate(action)
    }

}
