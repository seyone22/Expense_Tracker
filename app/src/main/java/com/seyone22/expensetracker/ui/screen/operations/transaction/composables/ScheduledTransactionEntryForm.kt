package com.seyone22.expensetracker.ui.screen.operations.transaction.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.RepeatFrequency
import com.seyone22.expensetracker.ui.screen.operations.transaction.BillsDepositsDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledTransactionEntryForm(
    modifier: Modifier = Modifier,
    editableTransactionDetails: BillsDepositsDetails,
    onValueChange: (TransactionDetails, BillsDepositsDetails) -> Unit,
    setRecurring: () -> Unit,
    viewModel: TransactionEntryViewModel,
    coroutineScope: CoroutineScope,
    focusManager: FocusManager = LocalFocusManager.current,
    edit: Boolean
) {
    var showRecurringFields by remember { mutableStateOf(false) }
    var openDateDueDialog by remember { mutableStateOf(false) }
    var allowAutomaticExecute by remember { mutableStateOf(false) }
    var promptUserConfirmation by remember { mutableStateOf(false) }

    var statusExpanded by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showRecurringFields = !showRecurringFields
                    setRecurring()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recurring Transaction",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
            Switch(
                checked = showRecurringFields,
                onCheckedChange = {
                    showRecurringFields = it
                    setRecurring()
                }
            )
        }

        if (showRecurringFields) {
            // Due date
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = true) {
                        openDateDueDialog = true
                    },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    //For Icons
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                value = editableTransactionDetails.NEXTOCCURRENCEDATE,
                onValueChange = {
                    onValueChange(
                        viewModel.transactionUiState.value.transactionDetails,
                        editableTransactionDetails.copy(NEXTOCCURRENCEDATE = it)
                    )
                },
                label = { Text("Date Due *") },
                readOnly = true,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            // Repeat Frequency Dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = true) { statusExpanded = true }
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = editableTransactionDetails.REPEATS,
                    readOnly = true,
                    onValueChange = {
                        onValueChange(
                            viewModel.transactionUiState.value.transactionDetails,
                            editableTransactionDetails.copy(REPEATS = it)
                        )
                    },
                    label = { Text("Transaction Status *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                )

                // Payments left
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false },
                ) {
                    enumValues<RepeatFrequency>().forEach { repeatFrequency ->
                        DropdownMenuItem(text = { Text(repeatFrequency.displayName) }, onClick = {
                            onValueChange(
                                viewModel.transactionUiState.value.transactionDetails,
                                editableTransactionDetails.copy(REPEATS = repeatFrequency.displayName)
                            )
                            statusExpanded = false
                        })
                    }
                }
            }
            // Automatic Execute
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = allowAutomaticExecute,
                    onCheckedChange = { allowAutomaticExecute = !allowAutomaticExecute },
                    enabled = !promptUserConfirmation
                )
                Text(
                    text = "Automatically execute the transaction on the due date",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Notify me to confirm execution
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = promptUserConfirmation, onCheckedChange = {
                        promptUserConfirmation = !promptUserConfirmation
                    }, enabled = !allowAutomaticExecute
                )
                Text(
                    text = "Notify me before executing",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = editableTransactionDetails.NUMOCCURRENCES,
                onValueChange = {
                    onValueChange(
                        viewModel.transactionUiState.value.transactionDetails,
                        editableTransactionDetails.copy(NUMOCCURRENCES = it)
                    )
                },
                label = { Text("Payments Left") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }

    if (openDateDueDialog) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDateDueDialog = false
        },

            confirmButton = {
                TextButton(
                    onClick = {
                        openDateDueDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)

                        onValueChange(
                            viewModel.transactionUiState.value.transactionDetails,
                            editableTransactionDetails.copy(
                                NEXTOCCURRENCEDATE = dateFormat.format(
                                    date
                                )
                            ),
                        )
                    }, enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openDateDueDialog = false
                }) {
                    Text("Cancel")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }
}