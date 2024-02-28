package pl.tinks.budgetbuddy.payment.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.MaterialToolbar
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
import pl.tinks.budgetbuddy.payment.list.PaymentListViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val DATE_PICKER_ADD_PAYMENT = "DATE_PICKER_ADD_PAYMENT"

@AndroidEntryPoint
class AddPaymentFragment : DialogFragment() {

    private val viewModel: PaymentListViewModel by activityViewModels()
    private lateinit var binding: FragmentAddPaymentBinding
    private lateinit var paymentTitleEditText: EditText
    private lateinit var paymentAmountEditText: EditText
    private lateinit var paymentDateEditText: EditText
    private lateinit var paymentFrequencyTextView: AutoCompleteTextView
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var selectedDate: LocalDateTime
    private lateinit var paymentFrequencies: Map<PaymentFrequency, String>
    private lateinit var toolbar: MaterialToolbar
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
        paymentFrequencies = getFrequencies()
        toolbar = binding.toolbarAddPayment

        toolbar.inflateMenu(R.menu.add_payment_menu)
        toolbar.menu.findItem(R.id.action_save).setVisible(false)

        paymentFrequencyTextView.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_payment_frequency,
                paymentFrequencies.values.toList()
            )
        )

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save -> {
                    addPayment()
                    dismiss()
                    true
                }

                else -> false
            }
        }

        toolbar.setNavigationOnClickListener { dismiss() }

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

        paymentTitleEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentAmountEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentDateEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentFrequencyTextView.addTextChangedListener(validateFieldsTextWatcher)

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

    private fun areFieldsValid(): Boolean {

        val valid: Boolean

        val title = paymentTitleEditText.text.toString()
        val amount = paymentAmountEditText.text.toString()
        val date = paymentDateEditText.text.toString()
        val frequency = paymentFrequencyTextView.text.toString()

        valid = !(title.isBlank() ||
                amount.isBlank() ||
                date.isBlank() ||
                frequency.isBlank())

        return valid
    }

    private fun getFrequencies(): Map<PaymentFrequency, String> {
        return mapOf(
            PaymentFrequency.SINGLE_PAYMENT to getString(R.string.single_payment),
            PaymentFrequency.DAILY to getString(R.string.daily),
            PaymentFrequency.WEEKLY to getString(R.string.weekly),
            PaymentFrequency.MONTHLY to getString(R.string.monthly),
            PaymentFrequency.QUARTERLY to getString(R.string.quarterly),
            PaymentFrequency.BIANNUALLY to getString(R.string.biannually),
            PaymentFrequency.ANNUALLY to getString(R.string.annually),
        )
    }

    private val validateFieldsTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {

            val valid = areFieldsValid()

            if (valid) {
                toolbar.menu.findItem(R.id.action_save).setVisible(true)
            } else {
                toolbar.menu.findItem(R.id.action_save).setVisible(false)
            }
        }

    }

}
