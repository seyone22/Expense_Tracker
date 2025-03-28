package com.seyone22.expensetracker.ui.screen.operations.account.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.data.model.AccountTypes
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetails
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEntryForm(
    modifier: Modifier = Modifier,
    accountDetails: AccountDetails,
    onValueChange: (AccountDetails) -> Unit,
    accountId: String?,
) {
    var accountTypeExpanded by remember { mutableStateOf(false) }
    var baseCurrencyExpanded by remember { mutableStateOf(false) }
    var openInitialDateDialog by remember { mutableStateOf(false) }
    var openPaymentDueDateDialog by remember { mutableStateOf(false) }
    var openStatementDateDialog by remember { mutableStateOf(false) }

    var showMore by remember { mutableStateOf(false) }

    val viewModel: AccountEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val focusManager = LocalFocusManager.current

    val currencyList by viewModel.currencyList.collectAsState()
    val baseCurrencyId by viewModel.baseCurrencyId.collectAsState()

    LaunchedEffect(baseCurrencyId) {
        onValueChange(accountDetails.copy(currencyId = baseCurrencyId.toString()))
        if (accountId != null) {
            viewModel.setAccount(accountId)
        }
    }

    Column(
        modifier = modifier
            .focusGroup()
            .fillMaxWidth(0.75f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdownMenuBox(expanded = baseCurrencyExpanded,
            onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = true) { baseCurrencyExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = (currencyList.currenciesList.find { it.currencyId == accountDetails.currencyId.toInt() })?.currencyName
                    ?: "",
                readOnly = true,
                onValueChange = { },
                label = { Text("Base Currency") },
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baseCurrencyExpanded) },
            )

            ExposedDropdownMenu(
                expanded = baseCurrencyExpanded,
                onDismissRequest = { baseCurrencyExpanded = false },
            ) {
                currencyList.currenciesList.forEach { currency ->
                    DropdownMenuItem(text = { Text(currency.currencyName) }, onClick = {
                        onValueChange(accountDetails.copy(currencyId = currency.currencyId.toString()))
                        baseCurrencyExpanded = false
                    })
                }
            }
        }
        ExposedDropdownMenuBox(expanded = accountTypeExpanded,
            onExpandedChange = { accountTypeExpanded = !accountTypeExpanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = true) { accountTypeExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = accountDetails.accountType,
                readOnly = true,
                onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
                label = { Text("Account Name *") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountTypeExpanded) },
            )

            ExposedDropdownMenu(
                expanded = accountTypeExpanded,
                onDismissRequest = { accountTypeExpanded = false },
            ) {
                enumValues<AccountTypes>().forEach { accountType ->
                    val displayName: String = accountType.displayName
                    DropdownMenuItem(text = { Text(displayName) }, onClick = {
                        onValueChange(accountDetails.copy(accountType = accountType.displayName))
                        accountTypeExpanded = false
                    })
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),

            value = accountDetails.accountName,
            onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
            label = { Text("Account Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = accountDetails.initialBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialBalance = it)) },
            label = { Text("Initial Balance *") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = true) {
                    openInitialDateDialog = true
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
            value = accountDetails.initialDate!!,
            onValueChange = { onValueChange(accountDetails.copy(initialDate = it)) },
            label = { Text("Opening Date  *") },
            readOnly = true,
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = accountDetails.notes.toString(),
            onValueChange = { onValueChange(accountDetails.copy(notes = it)) },
            label = { Text("Notes") },
            singleLine = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        if (!showMore) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    showMore = !showMore
                }
            ) {
                Text(text = "Show more fields")
            }
        }

        if (showMore) {
            Text(
                text = "Other Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.accountNum.toString(),
                onValueChange = { onValueChange(accountDetails.copy(accountNum = it)) },
                label = { Text("Account Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.heldAt.toString(),
                onValueChange = { onValueChange(accountDetails.copy(heldAt = it)) },
                label = { Text("Held At") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.website.toString(),
                onValueChange = { onValueChange(accountDetails.copy(website = it)) },
                label = { Text("Website") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.contactInfo.toString(),
                onValueChange = { onValueChange(accountDetails.copy(contactInfo = it)) },
                label = { Text("Contact Information") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.accessInfo.toString(),
                onValueChange = { onValueChange(accountDetails.copy(accessInfo = it)) },
                label = { Text("Access Information") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            Text(
                text = "Statement",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Statement Locked",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(CenterVertically)
                )
                Switch(
                    checked = accountDetails.statementLocked.toBoolean(),
                    onCheckedChange = { onValueChange(accountDetails.copy(statementLocked = it.toString())) },
                )

            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()

                    .clickable(enabled = true) {
                        openStatementDateDialog = true
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
                value = accountDetails.statementDate!!,
                onValueChange = { onValueChange(accountDetails.copy(statementDate = it)) },
                label = { Text("Statement Date") },
                readOnly = true,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.minimumBalance.toString(),
                onValueChange = { onValueChange(accountDetails.copy(minimumBalance = it)) },
                label = { Text("Minimum Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            Text(
                text = "Credit",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 32.dp)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.creditLimit.toString(),
                onValueChange = { onValueChange(accountDetails.copy(creditLimit = it)) },
                label = { Text("Credit Limit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })

            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.interestRate.toString(),
                onValueChange = { onValueChange(accountDetails.copy(interestRate = it)) },
                label = { Text("Interest Rate") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()

                    .clickable(enabled = true) {
                        openPaymentDueDateDialog = true
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
                value = accountDetails.paymentDueDate!!,
                onValueChange = { onValueChange(accountDetails.copy(paymentDueDate = it)) },
                label = { Text("Payment Due Date") },
                readOnly = true,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = accountDetails.minimumPayment.toString(),
                onValueChange = { onValueChange(accountDetails.copy(minimumPayment = it)) },
                label = { Text("Minimum Payment") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { })
            )
        }

        if (openInitialDateDialog) {
            val datePickerState = rememberDatePickerState()
            val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
            DatePickerDialog(onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openInitialDateDialog = false
            }, confirmButton = {
                TextButton(
                    onClick = {
                        openInitialDateDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)

                        onValueChange(accountDetails.copy(initialDate = dateFormat.format(date)))
                    }, enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openInitialDateDialog = false
                }) {
                    Text("Cancel")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }

    }
    if (openStatementDateDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openStatementDateDialog = false
        }, confirmButton = {
            TextButton(
                onClick = {
                    openStatementDateDialog = false
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = Date(datePickerState.selectedDateMillis!!)

                    onValueChange(accountDetails.copy(statementDate = dateFormat.format(date)))
                }, enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        }, dismissButton = {
            TextButton(onClick = {
                openStatementDateDialog = false
            }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }
    if (openPaymentDueDateDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openPaymentDueDateDialog = false
        }, confirmButton = {
            TextButton(
                onClick = {
                    openPaymentDueDateDialog = false
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = Date(datePickerState.selectedDateMillis!!)

                    onValueChange(accountDetails.copy(paymentDueDate = dateFormat.format(date)))
                }, enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        }, dismissButton = {
            TextButton(onClick = {
                openPaymentDueDateDialog = false
            }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }

}