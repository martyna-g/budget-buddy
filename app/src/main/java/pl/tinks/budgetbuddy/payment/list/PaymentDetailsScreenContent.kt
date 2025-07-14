package pl.tinks.budgetbuddy.payment.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.tinks.budgetbuddy.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailsScreenContent(
    state: PaymentDetailsUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onFrequencyChange: (PaymentFrequency) -> Unit,
    onNotificationChange: (Boolean) -> Unit,
    onSavePayment: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var freqDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (state.isEdit) "Edit Payment" else "Add Payment") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onSavePayment()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }

                })
        }, modifier = modifier.fillMaxWidth()
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = state.title,
                onValueChange = { onTitleChange(it) },
                label = { Text(stringResource(R.string.payment_title)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.amount,
                onValueChange = { onAmountChange(it.filter { ch -> ch.isDigit() || ch == '.' }) },
                label = { Text(stringResource(R.string.payment_amount)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = { },
                label = { Text(stringResource(R.string.next_payment_date)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (showDatePicker) {
                DatePickerModal(onDateSelected = { millis ->
                    if (millis != null) {
                        val selectedDate =
                            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        val dateString = LocalDateTime.of(selectedDate, LocalTime.MIDNIGHT)
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        onDateChange(dateString)
                    }
                    showDatePicker = false
                }, onDismiss = { showDatePicker = false })
            }

            ExposedDropdownMenuBox(expanded = freqDropdownExpanded,
                onExpandedChange = { freqDropdownExpanded = !freqDropdownExpanded }) {
                OutlinedTextField(value = state.frequency?.getLabel() ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.payment_frequency)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable, true),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqDropdownExpanded) })
                ExposedDropdownMenu(expanded = freqDropdownExpanded,
                    onDismissRequest = { freqDropdownExpanded = false }) {
                    PaymentFrequency.entries.forEach { freq ->
                        DropdownMenuItem(text = { Text(freq.getLabel()) }, onClick = {
                            onFrequencyChange(freq)
                            freqDropdownExpanded = false
                        })
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.payment_notification_switch_text))
                Spacer(Modifier.weight(1f))
                Switch(checked = state.notificationEnabled, onCheckedChange = {
                    onNotificationChange(it)
                })
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}
