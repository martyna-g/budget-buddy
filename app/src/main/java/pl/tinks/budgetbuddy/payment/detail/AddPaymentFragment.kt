package pl.tinks.budgetbuddy.payment.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentAddPaymentBinding

class AddPaymentFragment : DialogFragment() {

    private lateinit var binding: FragmentAddPaymentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity(), R.style.BudgetBuddy_FullScreenDialog)

        binding = FragmentAddPaymentBinding.inflate(layoutInflater)

        builder.setView(binding.root)

        return builder.create()
    }

}
