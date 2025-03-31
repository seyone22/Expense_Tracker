package com.seyone22.expensetracker.ui.screen.operations.transaction.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.dialogs.AddEditCategoryDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.AddEditPayeeDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
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
    viewModel: TransactionEntryViewModel,
    entityViewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope,
    edit: Boolean,
    focusManager: FocusManager = LocalFocusManager.current
) {
    val transactionUiState by viewModel.transactionUiState.collectAsState()
    val entityList by viewModel.entityList.collectAsState()

    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    var showMore by remember { mutableStateOf(false) }

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

    var openTransactionDateDialog by remember { mutableStateOf(false) }

    var currentAccount by remember { mutableStateOf(Account()) }
    var currentPayee by remember { mutableStateOf(Payee()) }
    var currentCategory by remember { mutableStateOf(Category()) }
    var currentToAccount by remember { mutableStateOf(Account()) }
    var currentAdvancedAmount by remember { mutableDoubleStateOf(if (transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName) transactionUiState.transactionDetails.toTransAmount.toDouble() else 0.0) }

    var currentCurrency: CurrencyFormat? by remember { mutableStateOf(null) }
    var currentCurrencyAdvanced: CurrencyFormat? by remember { mutableStateOf(null) }

    LaunchedEffect(currentAccount, currentToAccount) {
        coroutineScope.launch {
            if (transactionUiState.transactionDetails.accountId.isNotBlank()) {
                currentCurrency = sharedViewModel.getCurrencyById(currentAccount.currencyId)
            }
            if (transactionUiState.transactionDetails.toAccountId.isNotBlank()) {
                currentCurrencyAdvanced =
                    sharedViewModel.getCurrencyById(currentToAccount.currencyId)
            }
        }
    }

    if (edit) {
        coroutineScope.launch {
            currentAccount =
                viewModel.getAccount(transactionUiState.transactionDetails.accountId.toInt())
            currentPayee = viewModel.getPayee(transactionUiState.transactionDetails.payeeId.toInt())
            currentCategory =
                viewModel.getCategory(transactionUiState.transactionDetails.categoryId.toInt())
            currentToAccount =
                viewModel.getAccount(transactionUiState.transactionDetails.toAccountId.toInt())
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Transaction Type (transCode) Dropdown
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            enumValues<TransactionCode>().forEachIndexed { index, transactionCode ->
                SegmentedButton(
                    selected = transactionUiState.transactionDetails.transCode == transactionCode.displayName,
                    onClick = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(transCode = transactionCode.displayName),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    },
                    label = {
                        if (transactionCode.displayName == TransactionCode.WITHDRAWAL.displayName) Text(
                            "Withdraw"
                        ) else Text(transactionCode.displayName)
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = enumValues<TransactionCode>().size
                    )
                )
            }
        }

        // Transaction Date
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
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
            value = transactionUiState.transactionDetails.transDate,
            onValueChange = {
                viewModel.updateUiState(
                    transactionUiState.transactionDetails.copy(transDate = it),
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
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionStatus.entries.forEach { transactionStatus ->
                item {
                    FilterChip(
                        selected = transactionUiState.transactionDetails.status == transactionStatus.displayName,
                        leadingIcon = {
                            if (transactionUiState.transactionDetails.status == transactionStatus.displayName) {
                                Icon(Icons.Filled.Check, "")
                            }
                        },
                        onClick = {
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(status = transactionStatus.displayName),
                                viewModel.transactionUiState.value.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        },
                        label = { Text(transactionStatus.displayName) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Transaction From Dropdown
        ExposedDropdownMenuBox(expanded = accountExpanded, onExpandedChange = {
            accountExpanded = !accountExpanded
        }) {
            OutlinedTextField(
                modifier = Modifier
                    .clickable(enabled = true) { accountExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                value = currentAccount.accountName,
                readOnly = true,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(accountId = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = {
                    when (transactionUiState.transactionDetails.transCode) {
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
                entityList.accountsList.forEach { account ->
                    DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(accountId = account.accountId.toString()),
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
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Payee/To Dropdown
            ExposedDropdownMenuBox(expanded = payeeExpanded, onExpandedChange = {
                payeeExpanded = !payeeExpanded
                when (transactionUiState.transactionDetails.transCode) {
                    TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                    }

                    TransactionCode.TRANSFER.displayName -> {
                    }
                }
            }) {
                OutlinedTextField(
                    modifier = Modifier
                        .clickable(enabled = true) { payeeExpanded = true }
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = when (transactionUiState.transactionDetails.transCode) {
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
                        when (transactionUiState.transactionDetails.transCode) {
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
                    when (transactionUiState.transactionDetails.transCode) {
                        TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                            entityList.payeesList.forEach { payee ->
                                DropdownMenuItem(text = { Text(payee.payeeName) }, onClick = {
                                    currentPayee = payee
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            toAccountId = "-1",
                                            payeeId = currentPayee.payeeId.toString()
                                        ),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    payeeExpanded = false
                                })
                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            entityList.accountsList.forEach { account ->
                                DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            payeeId = "-1",
                                            toAccountId = account.accountId.toString()
                                        ),
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
                    viewModel.showDialog(
                        AddEditPayeeDialogAction(
                            onAdd = { payee ->
                                coroutineScope.launch {
                                    entityViewModel.savePayee(payee)
                                    viewModel.updatePayeesList()
                                    currentPayee = payee
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            toAccountId = "-1",
                                            payeeId = currentPayee.payeeId.toString()
                                        ),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                }
                            },
                            onEdit = { payee ->
                                coroutineScope.launch {
                                    entityViewModel.editPayee(payee)
                                    viewModel.updatePayeesList()
                                }
                            },
                        )
                    )
                },
                enabled = (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = transactionUiState.transactionDetails.transAmount,
            onValueChange = {
                if (transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName && currentCurrency != null && currentCurrencyAdvanced != null && it.isNotEmpty()) {
                    if (currentCurrency?.currencyId != currentCurrencyAdvanced?.currencyId) {
                        currentAdvancedAmount =
                            it.toDouble() * (currentCurrency?.baseConvRate!! * currentCurrencyAdvanced?.baseConvRate!!)
                    } else {
                        currentAdvancedAmount = it.toDouble()
                    }
                }
                viewModel.updateUiState(
                    transactionUiState.transactionDetails.copy(transAmount = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Amount*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentAdvancedAmount.toString(),
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails,
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
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = {
                categoryExpanded = !categoryExpanded
            }) {
                OutlinedTextField(
                    modifier = Modifier
                        .clickable(enabled = true) {
                            categoryExpanded = true
                        }
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = removeTrPrefix(currentCategory.categName),
                    readOnly = true,
                    onValueChange = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(categoryId = it),
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
                        entityList.categoriesList.filter { it.parentId == -1 }
                    val childCategoriesMap =
                        entityList.categoriesList.filter { it.parentId != -1 }
                            .groupBy { it.parentId } // Group children by parentId


                    for (parent in parentCategories) {
                        DropdownMenuItem(text = {
                            Row {
                                Text(removeTrPrefix(parent.categName))
                            }
                        }, onClick = {
                            currentCategory = parent
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(categoryId = parent.categId.toString()),
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
                                viewModel.updateUiState(
                                    transactionUiState.transactionDetails.copy(categoryId = child.categId.toString()),
                                    viewModel.transactionUiState.value.billsDepositsDetails,
                                    currentAdvancedAmount
                                )
                                categoryExpanded = false
                            })
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
                    viewModel.showDialog(
                        AddEditCategoryDialogAction(
                            onAdd = { category ->
                                coroutineScope.launch {
                                    entityViewModel.saveCategory(category)
                                    viewModel.updateCategoriesList()
                                    currentCategory = category
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(categoryId = category.categId.toString()),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                }
                            },
                            onEdit = { category ->
                                coroutineScope.launch {
                                    entityViewModel.editCategory(category)
                                    viewModel.updateCategoriesList()
                                }
                            },
                        )
                    )
                },
                enabled = (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
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

        if (!showMore) {
            TextButton(onClick = { showMore = !showMore }) {
                Text(text = "Show more fields")
            }
        } else {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.transactionNumber,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(transactionNumber = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.notes,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(notes = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Notes") })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.color,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(color = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Color") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }

    if (openTransactionDateDialog) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
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

                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(
                                transDate = dateFormat.format(
                                    date
                                )
                            ),
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