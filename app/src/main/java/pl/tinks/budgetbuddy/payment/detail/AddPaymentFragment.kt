package pl.tinks.budgetbuddy.payment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentAddPaymentBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AddPaymentFragment : DialogFragment() {

    private lateinit var binding: FragmentAddPaymentBinding
    private lateinit var paymentTitleEditText: EditText
    private lateinit var paymentAmountEditText: EditText
    private lateinit var paymentDateEditText: EditText
    private lateinit var paymentFrequencyTextView: AutoCompleteTextView
    private lateinit var datePicker: MaterialDatePicker<Long>

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
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            paymentDateEditText.setText(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(dateInMillis), ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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
