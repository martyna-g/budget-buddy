package pl.tinks.budgetbuddy.payment.list

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.tinks.budgetbuddy.R

enum class PaymentFrequency(@StringRes val labelRes: Int) {
    SINGLE_PAYMENT(R.string.single_payment),
    DAILY(R.string.daily),
    WEEKLY(R.string.weekly),
    MONTHLY(R.string.monthly),
    QUARTERLY(R.string.quarterly),
    BIANNUALLY(R.string.biannually),
    ANNUALLY(R.string.annually)
}

@Composable
fun PaymentFrequency.getLabel(): String = stringResource(labelRes)
