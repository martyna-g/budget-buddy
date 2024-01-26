package pl.tinks.budgetbuddy.payment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import pl.tinks.budgetbuddy.DecimalDigitsInputFilter
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentAddPaymentBinding
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val DATE_PICKER_ADD_PAYMENT = "DATE_PICKER_ADD_PAYMENT"

@AndroidEntryPoint
class AddPaymentFragment : DialogFragment() {

    private val viewModel: AddPaymentViewModel by viewModels()
    private lateinit var binding: FragmentAddPaymentBinding
    private lateinit var paymentTitleEditText: EditText
    private lateinit var paymentAmountEditText: EditText
    private lateinit var paymentDateEditText: EditText
    private lateinit var paymentFrequencyTextView: AutoCompleteTextView
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var selectedDate: LocalDateTime
    private lateinit var paymentFrequencies: Map<PaymentFrequency, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        paymentTitleEditText = binding.textInputEditTextPaymentTitle
        paymentAmountEditText = binding.textInputEditTextPaymentAmount
        paymentDateEditText = binding.textInputEditTextPaymentDate
        paymentFrequencyTextView = binding.autocompleteTextviewPaymentFrequency
        paymentFrequencies = viewModel.getFrequencies()

        paymentFrequencyTextView.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_payment_frequency,
                paymentFrequencies.values.toList()
            )
        )

        with(paymentAmountEditText) {
            addTextChangedListener(DecimalDigitsInputFilter(this))
        }

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            selectedDate =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(dateInMillis), ZoneId.of("UTC"))

            paymentDateEditText.setText(
                selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
        }

        paymentDateEditText.setOnClickListener {
            datePicker.show(
                parentFragmentManager,
                DATE_PICKER_ADD_PAYMENT
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}
