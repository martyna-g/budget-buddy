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
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.DecimalDigitsInputFilter
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentAddPaymentBinding
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

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
    private val currencyGbp: CurrencyUnit = CurrencyUnit.GBP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

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

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        with(paymentAmountEditText) {
            addTextChangedListener(DecimalDigitsInputFilter(this))
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun addPayment() {

        val uuid = UUID.randomUUID()

        val title = paymentTitleEditText.text.toString()
        val amount = paymentAmountEditText.text.toString()
        val frequency = paymentFrequencyTextView.text.toString()

        val amountMoney: Money = Money.of(currencyGbp, amount.toDoubleOrNull() ?: 0.00)
        val paymentFrequency: PaymentFrequency =
            paymentFrequencies.entries.find { it.value == frequency }?.key!!

        viewModel.addPayment(
            Payment(
                uuid,
                title,
                amountMoney,
                selectedDate,
                paymentFrequency
            )
        )
    }

}
