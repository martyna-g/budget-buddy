package pl.tinks.budgetbuddy.payment.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.materialswitch.MaterialSwitch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import pl.tinks.budgetbuddy.DecimalDigitsInputFilter
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentPaymentDetailsBinding
import pl.tinks.budgetbuddy.payment.Payment
import pl.tinks.budgetbuddy.payment.PaymentFrequency
import pl.tinks.budgetbuddy.payment.list.PaymentListViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val DATE_PICKER_ADD_PAYMENT = "DATE_PICKER_ADD_PAYMENT"

@AndroidEntryPoint
class PaymentDetailsFragment : DialogFragment() {

    private val viewModel: PaymentListViewModel by activityViewModels()
    private lateinit var binding: FragmentPaymentDetailsBinding
    private lateinit var paymentTitleEditText: EditText
    private lateinit var paymentAmountEditText: EditText
    private lateinit var paymentDateEditText: EditText
    private lateinit var paymentFrequencyTextView: AutoCompleteTextView
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var selectedDate: LocalDateTime
    private lateinit var paymentFrequencies: Array<out String>
    private lateinit var notificationSwitch: MaterialSwitch
    private lateinit var toolbar: MaterialToolbar
    private val currencyGbp: CurrencyUnit = CurrencyUnit.GBP
    private val args: PaymentDetailsFragmentArgs by navArgs()
    private lateinit var actionButtonType: ActionButtonType
    private var paymentId: UUID? = null
    private lateinit var payment: Payment
    private var hasUnsavedChanges = false
    private var isInitializing = true
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(
                        context, R.string.payment_notification_permission_denied, Toast.LENGTH_SHORT
                    ).show()
                    notificationSwitch.isChecked = false
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        actionButtonType = args.buttonType
        paymentId = if (args.paymentId != null) UUID.fromString(args.paymentId) else null

        val constraintsBuilder =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())

        binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)

        paymentTitleEditText = binding.textInputEditTextPaymentTitle
        paymentAmountEditText = binding.textInputEditTextPaymentAmount
        paymentDateEditText = binding.textInputEditTextPaymentDate
        paymentFrequencyTextView = binding.autocompleteTextviewPaymentFrequency
        notificationSwitch = binding.switchPaymentNotification
        toolbar = binding.toolbarAddPayment
        paymentFrequencies = resources.getStringArray(R.array.payment_frequencies)

        toolbar.inflateMenu(R.menu.add_payment_menu)
        toolbar.menu.findItem(R.id.action_save).setVisible(false)

        paymentFrequencyTextView.setAdapter(
            ArrayAdapter(
                requireContext(), R.layout.item_payment_frequency, paymentFrequencies
            )
        )

        datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.select_date))
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

        if (paymentId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.initPaymentDetails(paymentId!!)
                viewModel.selectedPayment.collect {
                    populateFields(it)
                    payment = it
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.action_save -> {

                    if (actionButtonType == ActionButtonType.ADD) {
                        addPayment()
                        dismiss()
                    } else {
                        updatePayment(paymentId!!)
                        dismiss()
                    }
                    true
                }

                R.id.action_delete -> {
                    showDeleteConfirmationDialog(payment)
                    true
                }

                else -> false
            }
        }

        toolbar.setNavigationOnClickListener {
            if (hasUnsavedChanges) showDiscardChangesConfirmationDialog() else dismiss()
        }

        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            selectedDate =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(dateInMillis), ZoneId.of("UTC"))

            paymentDateEditText.setText(
                selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
        }

        paymentDateEditText.setOnClickListener {
            datePicker.show(
                parentFragmentManager, DATE_PICKER_ADD_PAYMENT
            )
        }

        binding.buttonCalendar.setOnClickListener {
            datePicker.show(
                parentFragmentManager, DATE_PICKER_ADD_PAYMENT
            )
        }

        with(paymentAmountEditText) {
            addTextChangedListener(DecimalDigitsInputFilter(this))
        }

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkAndRequestNotificationPermission()
        }

        paymentTitleEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentAmountEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentDateEditText.addTextChangedListener(validateFieldsTextWatcher)
        paymentFrequencyTextView.addTextChangedListener(validateFieldsTextWatcher)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun addPayment() {
        val id = UUID.randomUUID()
        viewModel.addPayment(createPaymentFromUserInput(id))
    }

    private fun updatePayment(id: UUID) {
        viewModel.updatePayment(createPaymentFromUserInput(id))
    }

    private fun deletePayment(payment: Payment) {
        viewModel.deletePayment(payment)
    }

    private fun createPaymentFromUserInput(id: UUID): Payment {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val title = paymentTitleEditText.text.toString()
        val amount = paymentAmountEditText.text.toString()
        val localDate = LocalDate.parse(paymentDateEditText.text, formatter)
        val frequency = paymentFrequencyTextView.text.toString()

        val amountMoney: Money = Money.of(currencyGbp, amount.toDoubleOrNull() ?: 0.00)
        val date = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
        val paymentFrequency: PaymentFrequency = mapStringToPaymentFrequency(frequency)
        val notificationEnabled = notificationSwitch.isChecked

        return Payment(
            id,
            title,
            amountMoney,
            date,
            paymentFrequency,
            notificationEnabled = notificationEnabled
        )
    }

    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationale()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    private fun showPermissionRationale() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.payment_notification_permission_title)
            .setMessage(R.string.payment_notification_permission_message)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
                notificationSwitch.isChecked = false
            }.show()
    }

    private fun populateFields(payment: Payment) {
        val frequency = mapPaymentFrequencyToString(payment.frequency)
        paymentFrequencyTextView.setText(frequency, false)
        paymentTitleEditText.setText(payment.title)
        paymentAmountEditText.setText(payment.amount.amount.toPlainString())
        paymentDateEditText.setText(
            payment.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
        notificationSwitch.isChecked = payment.notificationEnabled
        isInitializing = false
    }

    private fun mapStringToPaymentFrequency(frequencyInput: String): PaymentFrequency {
        return when (frequencyInput) {
            getString(R.string.single_payment) -> PaymentFrequency.SINGLE_PAYMENT
            getString(R.string.daily) -> PaymentFrequency.DAILY
            getString(R.string.weekly) -> PaymentFrequency.WEEKLY
            getString(R.string.monthly) -> PaymentFrequency.MONTHLY
            getString(R.string.quarterly) -> PaymentFrequency.QUARTERLY
            getString(R.string.biannually) -> PaymentFrequency.BIANNUALLY
            getString(R.string.annually) -> PaymentFrequency.ANNUALLY
            else -> throw IllegalArgumentException("Invalid payment frequency")
        }
    }

    private fun mapPaymentFrequencyToString(frequencyInput: PaymentFrequency): String {
        return when (frequencyInput) {
            PaymentFrequency.SINGLE_PAYMENT -> getString(R.string.single_payment)
            PaymentFrequency.DAILY -> getString(R.string.daily)
            PaymentFrequency.WEEKLY -> getString(R.string.weekly)
            PaymentFrequency.MONTHLY -> getString(R.string.monthly)
            PaymentFrequency.QUARTERLY -> getString(R.string.quarterly)
            PaymentFrequency.BIANNUALLY -> getString(R.string.biannually)
            PaymentFrequency.ANNUALLY -> getString(R.string.annually)
        }
    }

    private fun disableFieldInteractions() {
        paymentTitleEditText.isFocusable = false
        paymentAmountEditText.isFocusable = false
        paymentDateEditText.setOnClickListener(null)
        paymentFrequencyTextView.isFocusable = false
        paymentFrequencyTextView.setAdapter(null)

    }

    private fun showDeleteConfirmationDialog(payment: Payment) {
        AlertDialog.Builder(requireContext()).setMessage(R.string.delete_payment_message)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                deletePayment(payment)
                dialog.dismiss()
                dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showDiscardChangesConfirmationDialog() {
        AlertDialog.Builder(requireContext()).setMessage(R.string.discard_changes_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun areFieldsValid(): Boolean {

        val valid: Boolean

        val title = paymentTitleEditText.text.toString()
        val amount = paymentAmountEditText.text.toString()
        val date = paymentDateEditText.text.toString()
        val frequency = paymentFrequencyTextView.text.toString()

        valid = !(title.isBlank() || amount.isBlank() || date.isBlank() || frequency.isBlank())

        return valid
    }

    private val validateFieldsTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!isInitializing || paymentId == null) hasUnsavedChanges = true
        }

        override fun afterTextChanged(p0: Editable?) {

            val valid = areFieldsValid()

            if (valid) {
                when (actionButtonType) {
                    ActionButtonType.DELETE -> {
                        toolbar.menu.findItem(R.id.action_save).isVisible = false
                        toolbar.menu.findItem(R.id.action_delete).isVisible = true
                        disableFieldInteractions()
                    }

                    ActionButtonType.INFO -> {
                        toolbar.menu.findItem(R.id.action_save).isVisible = false
                        toolbar.menu.findItem(R.id.action_delete).isVisible = false
                        disableFieldInteractions()
                    }

                    else -> {
                        toolbar.menu.findItem(R.id.action_save).isVisible = true
                        toolbar.menu.findItem(R.id.action_delete).isVisible = false
                    }
                }
            } else {
                toolbar.menu.findItem(R.id.action_save).setVisible(false)
            }
        }

    }

    companion object {
        enum class ActionButtonType {
            INFO, EDIT, DELETE, ADD,
        }
    }

}
