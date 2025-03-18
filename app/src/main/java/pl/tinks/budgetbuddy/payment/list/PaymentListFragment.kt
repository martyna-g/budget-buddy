package pl.tinks.budgetbuddy.payment.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentPaymentListBinding
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentItem
import java.time.LocalDate
import java.util.UUID

@AndroidEntryPoint
class PaymentListFragment : Fragment() {

    private val viewModel: PaymentListViewModel by activityViewModels()
    private lateinit var binding: FragmentPaymentListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var paymentListAdapter: PaymentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerviewPaymentList
        toolbar = binding.toolbarPaymentList
        floatingActionButton = binding.fabPaymentList
        floatingActionButton.isVisible = false

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        floatingActionButton.setOnClickListener {
            navigateToPaymentDetailFragment(null, null)
        }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                topMargin = insets.top
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }

        paymentListAdapter = PaymentListAdapter(
            ::handleActionButtonClick, ::handleMoveToHistoryButtonClick
        )

        recyclerView.adapter = paymentListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PaymentUiState.Loading -> disableUserInteractions()
                        is PaymentUiState.Success -> {
                            enableUserInteractions()
                            setupAdapter(uiState.data)
                        }

                        is PaymentUiState.Error -> showErrorDialog()
                    }
                }
            }
        }
    }

    private fun setupAdapter(payments: List<Payment>) {
        val items = payments.groupBy {
            when {
                it.date.toLocalDate() < LocalDate.now() -> R.string.overdue_payments
                it.date.toLocalDate() == LocalDate.now() -> R.string.payments_due_today
                else -> R.string.upcoming_payments
            }
        }.flatMap { (category, paymentsInCategory) ->
            listOf(PaymentItem.Header(getString(category))) + paymentsInCategory
                .map { PaymentItem.PaymentEntry(it) }
        }

        paymentListAdapter.submitList(items)
    }

    private fun disableUserInteractions() {
        recyclerView.isVisible = false
        floatingActionButton.isVisible = false
    }

    private fun enableUserInteractions() {
        recyclerView.isVisible = true
        floatingActionButton.isVisible = true
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity()).setMessage(R.string.payments_loading_error_message)
            .setPositiveButton(R.string.dialog_ok, null).show()
    }

    private fun handleActionButtonClick(buttonId: Int, paymentId: UUID) {
        navigateToPaymentDetailFragment(buttonId, paymentId.toString())
    }

    private fun handleMoveToHistoryButtonClick(payment: Payment) {
        viewModel.moveToHistory(payment)

        val message = getString(R.string.payment_moved_to_history, payment.title)

        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).setAction(R.string.undo) {
            viewModel.undoMoveToHistory(payment)
        }.show()
    }

    private fun navigateToPaymentDetailFragment(buttonId: Int?, paymentId: String?) {
        val actionButtonType = when (buttonId) {
            R.id.button_item_info -> PaymentDetailsFragment.Companion.ActionButtonType.INFO
            R.id.button_item_edit -> PaymentDetailsFragment.Companion.ActionButtonType.EDIT
            R.id.button_item_delete -> PaymentDetailsFragment.Companion.ActionButtonType.DELETE
            else -> PaymentDetailsFragment.Companion.ActionButtonType.ADD
        }
        val action =
            PaymentListFragmentDirections.actionPaymentListFragmentToPaymentDetailsFragment(
                actionButtonType, paymentId
            )
        findNavController().navigate(action)
    }
}
