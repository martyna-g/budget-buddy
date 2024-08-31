package pl.tinks.budgetbuddy.payment.history

import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentPaymentHistoryBinding
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentHistoryAdapter

@AndroidEntryPoint
class PaymentHistoryFragment : Fragment() {

    private val viewModel: PaymentHistoryViewModel by viewModels()
    private lateinit var binding: FragmentPaymentHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    private var actionMode: ActionMode? = null
    private val selectedPayments = mutableListOf<Payment>()

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.payment_history_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
            when (menuItem?.itemId) {
                R.id.action_delete_selected_payments -> {
                    val paymentsToDelete = selectedPayments.toList()
                    showConfirmationDialog(paymentsToDelete.size, {
                        viewModel.deleteSelectedPayments(paymentsToDelete)
                    }, {
                        mode?.finish()
                    })
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            selectedPayments.clear()
            paymentHistoryAdapter.notifyDataSetChanged()
            actionMode = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        paymentHistoryAdapter = PaymentHistoryAdapter(
            ::onPaymentLongClicked,
            ::onPaymentClicked
        ) { payment -> selectedPayments.contains(payment) }

        binding = FragmentPaymentHistoryBinding.inflate(inflater, container, false)

        toolbar = binding.toolbarPaymentHistory
        recyclerView = binding.recyclerviewPaymentHistory
        recyclerView.adapter = paymentHistoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        navController.addOnDestinationChangedListener { _, _, _ ->
            actionMode?.finish()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is PaymentHistoryUiState.Loading -> disableUserInteractions()
                        is PaymentHistoryUiState.Error -> showErrorDialog()
                        is PaymentHistoryUiState.Success -> {
                            enableUserInteractions()
                            setupAdapter(uiState.data)
                        }
                    }
                }
            }
        }

    }

    private fun setupAdapter(payments: List<Payment>) {
        val items = mutableListOf<PaymentItem>()
        val groupedByMonth = payments.groupBy {
            "${it.date.month.toString().substring(0..2)} ${it.date.year}"
        }

        groupedByMonth.forEach { (month, paymentsInMonth) ->
            items.add(PaymentItem.Header(month))
            paymentsInMonth.forEach { items.add(PaymentItem.PaymentEntry(it)) }
        }

        paymentHistoryAdapter.submitList(items)
    }

    private fun onPaymentLongClicked(payment: Payment) {
        val index = paymentHistoryAdapter.currentList.indexOfFirst {
            it is PaymentItem.PaymentEntry && it.payment == payment
        }
        if (selectedPayments.isEmpty()) {
            selectedPayments.add(payment)
            startActionMode()
        } else {
            onPaymentClicked(payment)
        }
        paymentHistoryAdapter.notifyItemChanged(index)
    }

    private fun onPaymentClicked(payment: Payment) {
        if (actionMode != null) {
            val index = paymentHistoryAdapter.currentList.indexOfFirst {
                it is PaymentItem.PaymentEntry && it.payment == payment
            }
            if (selectedPayments.contains(payment)) {
                selectedPayments.remove(payment)
                if (selectedPayments.isEmpty()) {
                    finishActionMode()
                }
            } else {
                selectedPayments.add(payment)
            }
            paymentHistoryAdapter.notifyItemChanged(index)
        }
    }

    private fun startActionMode() {
        actionMode = requireActivity().startActionMode(actionModeCallback)
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }

    private fun disableUserInteractions() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
    }

    private fun enableUserInteractions() {
        recyclerView.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
    }

    private fun showConfirmationDialog(count: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
        val confirmationMessage = requireContext().resources.getQuantityString(
            R.plurals.delete_payment_history_message, count
        )

        AlertDialog.Builder(requireContext()).setMessage(confirmationMessage)
            .setPositiveButton(R.string.yes) { _, _ ->
                onConfirm()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.setOnDismissListener {
                onDismiss()
            }.show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity()).setMessage(R.string.payments_loading_error_message)
            .setPositiveButton(R.string.dialog_ok) { _, _ -> enableUserInteractions() }
            .show()
    }
}
