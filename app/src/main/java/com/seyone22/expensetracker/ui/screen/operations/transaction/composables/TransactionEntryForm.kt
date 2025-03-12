package com.seyone22.expensetracker.ui.screen.operations.transaction.composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.BillsDepositsDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryForm(
    modifier: Modifier = Modifier,
    transactionDetails: TransactionDetails,
    transactionUiState: TransactionUiState,
    onValueChange: (TransactionDetails, BillsDepositsDetails, Double) -> Unit,
    viewModel: TransactionEntryViewModel,
    coroutineScope: CoroutineScope,
    edit: Boolean,
    focusManager: FocusManager = LocalFocusManager.current
) {
    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var tagsExpanded by remember { mutableStateOf(false) }

    /*    var tagFilter by remember { mutableStateOf("") }
        val filteredTags = data.tags.filter {
            (it?.name ?: "").contains(
                tagFilter, true
            )
        }*/

    var openNewPayeeDialog by remember { mutableStateOf(false) }
    var openTransactionDateDialog by remember { mutableStateOf(false) }

    var currentAccount by remember { mutableStateOf(Account()) }
    var currentPayee by remember { mutableStateOf(Payee()) }
    var currentCategory by remember { mutableStateOf(Category()) }
    var currentToAccount by remember { mutableStateOf(Account()) }
    var currentAdvancedAmount by remember { mutableDoubleStateOf(if (transactionDetails.transCode == TransactionCode.TRANSFER.displayName) transactionDetails.toTransAmount.toDouble() else 0.0) }

    var currentCurrency: CurrencyFormat? by remember { mutableStateOf(null) }
    var currentCurrencyAdvanced: CurrencyFormat? by remember { mutableStateOf(null) }

    LaunchedEffect(currentAccount, currentToAccount) {
        coroutineScope.launch {
            if (transactionDetails.accountId.isNotBlank()) {
                currentCurrency = sharedViewModel.getCurrencyById(currentAccount.currencyId)
            }
            if (transactionDetails.toAccountId.isNotBlank()) {
                currentCurrencyAdvanced =
                    sharedViewModel.getCurrencyById(currentToAccount.currencyId)
            }
            Log.d("TAG", "Datum: $currentCurrency")
            Log.d("TAG", "Datum: $currentCurrencyAdvanced")
        }
    }


    if (edit) {
        coroutineScope.launch {
            currentAccount = viewModel.getAccount(transactionDetails.accountId.toInt())
            currentPayee = viewModel.getPayee(transactionDetails.payeeId.toInt())
            currentCategory = viewModel.getCategory(transactionDetails.categoryId.toInt())
            currentToAccount = viewModel.getAccount(transactionDetails.toAccountId.toInt())
        }
    }

    Column(
        modifier = modifier
            .focusGroup()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(modifier = Modifier
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
            onValueChange = {
                onValueChange(
                    transactionDetails.copy(transDate = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Date of Transaction *") },
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
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = transactionDetails.status,
                readOnly = true,
                onValueChange = {
                    onValueChange(
                        transactionDetails.copy(status = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
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

            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false },
            ) {
                enumValues<TransactionStatus>().forEach { transactionStatus ->
                    DropdownMenuItem(text = { Text(transactionStatus.displayName) }, onClick = {
                        onValueChange(
                            transactionDetails.copy(status = transactionStatus.displayName),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        statusExpanded = false
                    })
                }
            }
        }

        // Transaction Type (transCode) Dropdown
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .clickable(enabled = true) { typeExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = transactionDetails.transCode,
                readOnly = true,
                onValueChange = {
                    if (it == TransactionCode.TRANSFER.displayName) {
                        onValueChange(
                            transactionDetails.copy(payeeId = "-1", transCode = it),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    } else {
                        onValueChange(
                            transactionDetails.copy(toAccountId = "-1", transCode = it),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    }
                },
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
                    DropdownMenuItem(text = { Text(transCode.displayName) }, onClick = {
                        onValueChange(
                            transactionDetails.copy(transCode = transCode.displayName),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        typeExpanded = false
                    })
                }
            }
        }

        // Transaction From Dropdown
        ExposedDropdownMenuBox(expanded = accountExpanded, onExpandedChange = {
            accountExpanded = !accountExpanded
        }) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .clickable(enabled = true) { accountExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = currentAccount.accountName,
                readOnly = true,
                onValueChange = {
                    onValueChange(
                        transactionDetails.copy(accountId = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = {
                    when (transactionDetails.transCode) {
                        TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                            Text(text = "Account *")
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            Text(text = "From Account *")
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
                transactionUiState.accountsList.forEach { account ->
                    DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                        onValueChange(
                            transactionDetails.copy(accountId = account.accountId.toString()),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        currentAccount = account
                        accountExpanded = false
                    })
                }
            }
        }

        // Transaction Payee
        Row(
            modifier = Modifier.padding(0.dp, 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Payee/To Dropdown
            ExposedDropdownMenuBox(expanded = payeeExpanded, onExpandedChange = {
                payeeExpanded = !payeeExpanded
                when (transactionDetails.transCode) {
                    TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                    }

                    TransactionCode.TRANSFER.displayName -> {
                    }
                }
            }) {

                OutlinedTextField(
                    modifier = Modifier
                        .clickable(enabled = true) { payeeExpanded = true }
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .width(240.dp),
                    value = when (transactionDetails.transCode) {
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
                                Text(text = "Payee *")
                            }

                            TransactionCode.DEPOSIT.displayName -> {
                                Text(text = "From *")
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                Text(text = "To Account *")
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
                                "TransactionEntryForm: Executes Other! ${transactionUiState.payeesList}"
                            )
                            transactionUiState.payeesList.forEach { payee ->
                                DropdownMenuItem(text = { Text(payee.payeeName) }, onClick = {
                                    currentPayee = payee
                                    onValueChange(
                                        transactionDetails.copy(toAccountId = "-1"),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    onValueChange(
                                        transactionDetails.copy(payeeId = currentPayee.payeeId.toString()),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    Log.d(
                                        "DEBUG", "TransactionEntryForm: $transactionDetails"
                                    )
                                    payeeExpanded = false
                                })
                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            transactionUiState.accountsList.forEach { account ->
                                Log.d("DEBUG", "TransactionEntryForm: Executes! $account")
                                DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                                    onValueChange(
                                        transactionDetails.copy(payeeId = "-1"),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    onValueChange(
                                        transactionDetails.copy(toAccountId = account.accountId.toString()),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    currentToAccount = account
                                    payeeExpanded = false
                                })
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
                enabled = (transactionDetails.transCode != TransactionCode.TRANSFER.displayName)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = transactionDetails.transAmount,
            onValueChange = {
                Log.d("TAG", "TransactionEntryForm: $currentCurrency")
                Log.d("TAG", "TransactionEntryForm: $currentCurrencyAdvanced")

                if (transactionDetails.transCode == TransactionCode.TRANSFER.displayName && currentCurrency != null && currentCurrencyAdvanced != null && it.isNotEmpty()) {
                    Log.d("TAG", "TransactionEntryForm: I'm here")

                    if (currentCurrency?.currencyId != currentCurrencyAdvanced?.currencyId) {
                        currentAdvancedAmount =
                            it.toDouble() * (currentCurrency?.baseConvRate!! * currentCurrencyAdvanced?.baseConvRate!!)
                    } else {
                        currentAdvancedAmount = it.toDouble()
                    }
                }
                onValueChange(
                    transactionDetails.copy(transAmount = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Amount*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (transactionDetails.transCode == TransactionCode.TRANSFER.displayName) {
            OutlinedTextField(
                value = currentAdvancedAmount.toString(),
                onValueChange = {
                    onValueChange(
                        transactionDetails,
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        it.toDouble()
                    )
                    currentAdvancedAmount = it.toDouble()


                },
                label = { Text("Deposit amount*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // Transaction Categories
        ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = {
            categoryExpanded = !categoryExpanded
        }) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .clickable(enabled = true) {
                        categoryExpanded = true
                    }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                value = removeTrPrefix(currentCategory.categName),
                readOnly = true,
                onValueChange = {
                    onValueChange(
                        transactionDetails.copy(categoryId = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
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
                val parentCategories =
                    transactionUiState.categoriesList.filter { it.parentId == -1 }
                val childCategoriesMap =
                    transactionUiState.categoriesList.filter { it.parentId != -1 }
                        .groupBy { it.parentId } // Group children by parentId


                for (parent in parentCategories) {
                    DropdownMenuItem(text = {
                        Row {
                            Text(removeTrPrefix(parent.categName))
                        }
                    }, onClick = {
                        currentCategory = parent
                        onValueChange(
                            transactionDetails.copy(categoryId = parent.categId.toString()),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        categoryExpanded = false
                    })

                    childCategoriesMap[parent.categId]?.forEach { child ->
                        DropdownMenuItem(text = {
                            Row {
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(removeTrPrefix(child.categName))
                            }
                        }, onClick = {
                            currentCategory = child
                            onValueChange(
                                transactionDetails.copy(categoryId = child.categId.toString()),
                                viewModel.transactionUiState.value.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                            categoryExpanded = false
                        })
                    }
                }
            }
        }

        /*        ExposedDropdownMenuBox(expanded = tagsExpanded, onExpandedChange = {
                    tagsExpanded = !tagsExpanded
                }) {
                    OutlinedTextField(modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .clickable(enabled = true) {
                            tagsExpanded = true
                        },
                        value = tagFilter,
                        readOnly = false,
                        onValueChange = { tagFilter = it },
                        label = { Text("Select a tag") },
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagsExpanded)
                        })

                    ExposedDropdownMenu(expanded = tagsExpanded, onDismissRequest = { }) {
                        if (filteredTags.isNotEmpty()) {
                            filteredTags.forEach { tag ->
                                tag?.let {
                                    DropdownMenuItem(text = { Text(tag.name) }, onClick = {
                                        // Check if the tag is already in the list
                                        if (!recipeTags.any { it.id == tag.id }) {
                                            // Add the new tag if it doesn't exist
                                            recipeTags = recipeTags + tag
                                        }
                                        tagFilter = ""
                                        tagsExpanded = false
                                    })
                                }
                            }
                        } else {
                            DropdownMenuItem(text = { Text("Add $tagFilter to database") },
                                onClick = {
                                    navController.navigate("Add Tag/$tagFilter")
                                })
                        }
                    }
                }
                LazyRow {
                    recipeTags.forEach { tag ->
                        item(key = tag.id) {
                            FilterChip(
                                modifier = Modifier.padding(end= 4.dp),
                                selected = true,  // Chips are not selected by default
                                onClick = {
                                    // Remove the tag from the list when clicked
                                    recipeTags = recipeTags.filter { it != tag }
                                }, label = {
                                    Text(text = tag.name)
                                }, trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,  // Close icon for the chip
                                        contentDescription = "Remove Tag"
                                    )
                                })
                        }
                    }
                }*/

        OutlinedTextField(value = transactionDetails.transactionNumber,
            onValueChange = {
                onValueChange(
                    transactionDetails.copy(transactionNumber = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(value = transactionDetails.notes, onValueChange = {
            onValueChange(
                transactionDetails.copy(notes = it),
                viewModel.transactionUiState.value.billsDepositsDetails,
                currentAdvancedAmount
            )
        }, label = { Text("Notes") })

        OutlinedTextField(value = transactionDetails.color,
            onValueChange = {
                onValueChange(
                    transactionDetails.copy(color = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Color") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }


    if (openNewPayeeDialog) {
        PayeeEntryDialog(onDismissRequest = { openNewPayeeDialog = false },
            viewModel = viewModel,
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.savePayee()
                }
            })
    }

    if (openTransactionDateDialog) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(onDismissRequest = {
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

                        onValueChange(
                            transactionDetails.copy(transDate = dateFormat.format(date)),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    }, enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openTransactionDateDialog = false
                }) {
                    Text("Cancel")
                }
            }) {
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
    val payeeSelected by remember { mutableStateOf(selectedPayee) }

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
    Dialog(onDismissRequest = { onDismissRequest() }) {
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
                    text = title, style = MaterialTheme.typography.titleLarge
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
                    modifier = Modifier.fillMaxWidth(),
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