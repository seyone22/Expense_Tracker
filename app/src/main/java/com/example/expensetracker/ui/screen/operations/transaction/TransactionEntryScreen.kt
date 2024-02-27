package com.example.expensetracker.ui.screen.operations.transaction

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TransactionEntryDestination : NavigationDestination {
    override val route = "TransactionEntry"
    override val titleRes = R.string.app_name
    override val routeId = 11
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(
                        text = "Create Transaction",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveTransaction()
                                navigateBack()
                            }
                        },
                        enabled = viewModel.transactionUiState.isEntryValid,
                        modifier = modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { padding ->
        TransactionEntryForm(
            transactionDetails = viewModel.transactionUiState.transactionDetails,
            onValueChange = viewModel::updateUiState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            viewModel = viewModel,
            coroutineScope = coroutineScope
        )
    }
}

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryForm(
    modifier: Modifier = Modifier,
    transactionDetails: TransactionDetails,
    onValueChange: (TransactionDetails) -> Unit = {},
    viewModel: TransactionEntryViewModel,
    coroutineScope: CoroutineScope
) {
    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var openNewPayeeDialog by remember { mutableStateOf(false) }

    var openTransactionDateDialog by remember { mutableStateOf(false) }

    //TODO: Refactor this to be more elegant
    val transactionUiState: TransactionUiState by viewModel.transactionUiState1.collectAsState()

    val focusManager = LocalFocusManager.current

    var currentAccount by remember { mutableStateOf(Account()) }
    var currentPayee by remember { mutableStateOf(Payee()) }
    var currentCategory by remember { mutableStateOf(Category()) }
    var currentToAccount by remember { mutableStateOf(Account()) }

    LazyColumn(
        modifier = modifier
            .focusGroup()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .clickable(enabled = true) {
                        openTransactionDateDialog = true
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
                value = transactionDetails.transDate,
                onValueChange = { onValueChange(transactionDetails.copy(transDate = it)) },
                label = { Text("Date of Transaction") },
                readOnly = true,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            // Transaction Status Dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) { statusExpanded = true }
                        .menuAnchor(),
                    value = transactionDetails.status,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(status = it)) },
                    label = { Text("Transaction Status *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false },
                ) {
                    enumValues<TransactionStatus>().forEach { transactionStatus ->
                        DropdownMenuItem(
                            text = { Text(transactionStatus.displayName) },
                            onClick = {
                                onValueChange(transactionDetails.copy(status = transactionStatus.displayName))
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = transactionDetails.transAmount,
                onValueChange = { onValueChange(transactionDetails.copy(transAmount = it)) },
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Transaction Type (transCode) Dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) { typeExpanded = true }
                        .menuAnchor(),
                    value = transactionDetails.transCode,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(transCode = it)) },
                    label = { Text("Transaction Type *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false },
                ) {
                    enumValues<TransactionCode>().forEach { transCode ->
                        DropdownMenuItem(
                            text = { Text(transCode.displayName) },
                            onClick = {
                                onValueChange(transactionDetails.copy(transCode = transCode.displayName))
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Transaction From Dropdown
            ExposedDropdownMenuBox(
                expanded = accountExpanded,
                onExpandedChange = {
                    accountExpanded = !accountExpanded
                    coroutineScope.launch {
                        viewModel.getAllAccounts()
                    }
                }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) { accountExpanded = true }
                        .menuAnchor(),
                    value = currentAccount.accountName,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(accountId = it)) },
                    label = {
                        when (transactionDetails.transCode) {
                            TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                                Text(text = "Account")
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                Text(text = "From Account")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = accountExpanded,
                    onDismissRequest = { accountExpanded = false },
                ) {
                    viewModel.transactionUiState.accountsList.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.accountName) },
                            onClick = {
                                onValueChange(transactionDetails.copy(accountId = account.accountId.toString()))
                                currentAccount = account
                                accountExpanded = false
                            }
                        )
                    }
                }
            }

            // Transaction Payee
            Row(
                modifier = Modifier
                    .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transaction Payee/To Dropdown
                ExposedDropdownMenuBox(
                    expanded = payeeExpanded,
                    onExpandedChange = {
                        payeeExpanded = !payeeExpanded
                        when (transactionDetails.transCode) {
                            TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {

                                coroutineScope.launch {
                                    Log.d("DEBUG", "BEORE GETALLPAYEES")

                                    viewModel.getAllPayees()
                                    Log.d(
                                        "DEBUG",
                                        "Within When: ${viewModel.transactionUiState2.payeesList}"
                                    )
                                }
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                coroutineScope.launch {
                                    viewModel.getAllAccounts()
                                }
                            }
                        }
                    }) {

                    OutlinedTextField(
                        modifier = Modifier
                            .clickable(enabled = true) { payeeExpanded = true }
                            .menuAnchor()
                            .width(240.dp),
                        value =
                        when (transactionDetails.transCode) {
                            TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                                currentPayee.payeeName
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                currentToAccount.accountName
                            }

                            else -> {
                                ""
                            }
                        },
                        readOnly = true,
                        onValueChange = { },
                        label = {
                            when (transactionDetails.transCode) {
                                TransactionCode.WITHDRAWAL.displayName -> {
                                    Text(text = "Payee")
                                }

                                TransactionCode.DEPOSIT.displayName -> {
                                    Text(text = "From")
                                }

                                TransactionCode.TRANSFER.displayName -> {
                                    Text(text = "To Account")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.moveFocus(
                                FocusDirection.Next
                            )
                        }),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payeeExpanded) },
                    )

                    ExposedDropdownMenu(
                        expanded = payeeExpanded,
                        onDismissRequest = { payeeExpanded = false },
                    ) {
                        when (transactionDetails.transCode) {
                            TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                                Log.d(
                                    "DEBUG",
                                    "TransactionEntryForm: Executes Other! ${viewModel.transactionUiState2.payeesList}"
                                )
                                viewModel.transactionUiState2.payeesList.forEach { payee ->
                                    DropdownMenuItem(
                                        text = { Text(payee.payeeName) },
                                        onClick = {
                                            currentPayee = payee
                                            onValueChange(transactionDetails.copy(toAccountId = "-1"))
                                            onValueChange(transactionDetails.copy(payeeId = currentPayee.payeeId.toString()))
                                            Log.d(
                                                "DEBUG",
                                                "TransactionEntryForm: $transactionDetails"
                                            )
                                            payeeExpanded = false
                                        }
                                    )
                                }
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                viewModel.transactionUiState.accountsList.forEach { account ->
                                    Log.d("DEBUG", "TransactionEntryForm: Executes! $account")
                                    DropdownMenuItem(
                                        text = { Text(account.accountName) },
                                        onClick = {
                                            onValueChange(transactionDetails.copy(payeeId = "-1"))
                                            onValueChange(transactionDetails.copy(toAccountId = account.accountId.toString()))
                                            currentToAccount = account
                                            payeeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                IconButton(
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .padding(10.dp, 10.dp, 0.dp, 0.dp),
                    onClick = {
                        openNewPayeeDialog = true
                    },
                    enabled = (transactionDetails.transCode != TransactionCode.TRANSFER.displayName)) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            }

            // Transaction Categories
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                    coroutineScope.launch {
                        viewModel.getAllAccounts()
                    }
                })
            {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) {
                            categoryExpanded = true
                            coroutineScope.launch {
                                viewModel.getAllAccounts()
                            }
                        }
                        .menuAnchor(),
                    value = removeTrPrefix(currentCategory.categName),
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(categoryId = it)) },
                    label = { Text("Transaction Category *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    transactionUiState.categoriesList.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(removeTrPrefix(category.categName)) },
                            onClick = {
                                currentCategory = category
                                onValueChange(transactionDetails.copy(categoryId = category.categId.toString()))
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = transactionDetails.transactionNumber,
                onValueChange = { onValueChange(transactionDetails.copy(transactionNumber = it)) },
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = transactionDetails.notes,
                onValueChange = { onValueChange(transactionDetails.copy(notes = it)) },
                label = { Text("Notes") }
            )

            OutlinedTextField(
                value = transactionDetails.color,
                onValueChange = { onValueChange(transactionDetails.copy(color = it)) },
                label = { Text("Color") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }


    if (openNewPayeeDialog) {
        PayeeEntryDialog(
            onDismissRequest = { openNewPayeeDialog = false },
            viewModel = viewModel,
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.savePayee()
                }
            }
        )
    }

    if (openTransactionDateDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openTransactionDateDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openTransactionDateDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)

                        onValueChange(transactionDetails.copy(transDate = dateFormat.format(date)))
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openTransactionDateDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun PayeeEntryDialog(
    modifier: Modifier = Modifier,
    title: String = "Add Payee",
    selectedPayee: PayeeDetails = PayeeDetails(payeeName = ""),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: TransactionEntryViewModel,
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var payeeSelected by remember { mutableStateOf(selectedPayee) }

    viewModel.updatePayeeState(
        viewModel.payeeUiState.payeeDetails.copy(
            payeeName = payeeSelected.payeeName,
            payeeId = payeeSelected.payeeId,
            categId = payeeSelected.categId,
            number = payeeSelected.number,
            website = payeeSelected.website,
            notes = payeeSelected.notes,
            active = payeeSelected.active
        )
    )
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(535.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.payeeName,
                    onValueChange = {
                        payeeSelected.payeeName = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                payeeName = it
                            )
                        )
                    },
                    label = { Text("Payee Name *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                Row(
                    modifier = Modifier.padding(0.dp, 8.dp),
                ) {
                    Checkbox(
                        checked = payeeSelected.active.toBoolean(),
                        onCheckedChange = {
                            payeeSelected.active = it.toString()
                            viewModel.updatePayeeState(
                                viewModel.payeeUiState.payeeDetails.copy(
                                    active = (it).toString()
                                )
                            )
                        },
                    )
                    Text(
                        text = "Hidden",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                // We're obviously not including last used category -_-
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.number,
                    onValueChange = {
                        payeeSelected.number = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                number = it
                            )
                        )
                    },
                    label = { Text("Reference Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.website,
                    onValueChange = {
                        payeeSelected.website = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                website = it
                            )
                        )
                    },
                    label = { Text("Website") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.notes,
                    onValueChange = {
                        payeeSelected.notes = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                notes = it
                            )
                        )
                    },
                    label = { Text("Notes") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirmClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = viewModel.payeeUiState.isEntryValid
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }
}