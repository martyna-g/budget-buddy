package pl.tinks.budgetbuddy

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class DecimalDigitsInputFilter(private val editText: EditText) : TextWatcher {

    private var isUpdating = false

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(input: Editable?) {

        if (isUpdating) return

        isUpdating = true

        val text = input.toString()

        val dotIndex = text.indexOf('.')

        val decimalPart = text.substring(dotIndex + 1)

        if (text.startsWith("0") && !text.startsWith("0.")) {
            editText.setText("0")
            editText.setSelection(1)

        } else if (text.contains(".") && decimalPart.length > 2) {
            editText.setText(text.substring(0, dotIndex + 3))
            editText.setSelection(dotIndex + 3)
        }

        isUpdating = false

    }
}
