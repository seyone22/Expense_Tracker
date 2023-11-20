package com.example.expensetracker.ui.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.entities.EntitiesUiState
import kotlinx.coroutines.launch

object TransactionEntryDestination : NavigationDestination {
    override val route = "EnterTransaction"
    override val titleRes = R.string.app_name
}

@Composable
fun TransactionEntryScreen(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = {
        viewModel.reset()
        onDismissRequest()
    }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TransactionEntryForm(
                    transactionDetails = viewModel.transactionUiState.transactionDetails,
                    onValueChange = viewModel::updateUiState,
                    modifier = Modifier.fillMaxWidth(),
                    viewModel = viewModel
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
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveTransaction()
                                onConfirmation()
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = viewModel.transactionUiState.isEntryValid,
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryForm(
    modifier: Modifier = Modifier,
    transactionDetails: TransactionDetails,
    onValueChange: (TransactionDetails) -> Unit = {},
    viewModel: TransactionEntryViewModel,

    ) {
    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    //TODO: Refactor this to be more elegant
    val transactionUiState1 : TransactionUiState by viewModel.transactionUiState1.collectAsState()
    val transactionUiState2 : TransactionUiState by viewModel.transactionUiState2.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier
            .focusGroup()
    ) {
        item {
            Button(onClick = {
                coroutineScope.launch {
                    viewModel.saveTransaction()
                }
            }) {

            }
            
            OutlinedTextField(
                value = transactionDetails.transDate,
                onValueChange = { onValueChange(transactionDetails.copy(transDate = it)) },
                label = { Text("Transaction Date") }
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
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
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
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
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
                    value = transactionDetails.accountId,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(accountId = it)) },
                    label = {
                        when (transactionDetails.transCode) {
                            TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                                Text(text = "Account")
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                Text(text = "To Account")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
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
                                accountExpanded = false
                            }
                        )
                    }
                }
            }

            // Transaction Payee/To Dropdown
            ExposedDropdownMenuBox(
                expanded = payeeExpanded,
                onExpandedChange = {
                    payeeExpanded = !payeeExpanded
                    coroutineScope.launch {
                        viewModel.getAllAccounts()
                    }
                    when (transactionDetails.payeeId) {
                        TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                            coroutineScope.launch {

                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            coroutineScope.launch {

                            }
                        }
                    }
                }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) { payeeExpanded = true }
                        .menuAnchor(),
                    value = transactionDetails.payeeId,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(payeeId = it)) },
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
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payeeExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = payeeExpanded,
                    onDismissRequest = { payeeExpanded = false },
                ) {
                    when (transactionDetails.transCode) {
                        TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                            transactionUiState2.payeesList.forEach { payee ->
                                DropdownMenuItem(
                                    text = { Text(payee.payeeName) },
                                    onClick = {
                                        onValueChange(transactionDetails.copy(payeeId = payee.payeeId.toString()))
                                        onValueChange(transactionDetails.copy(toAccountId = "-1"))
                                        payeeExpanded = false
                                    }
                                )
                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            /*                    viewModel.transactionUiState.accountsList.forEach { account ->
                                                    val displayName: String = account.accountName
                                                    DropdownMenuItem(
                                                        text = { Text(displayName) },
                                                        onClick = {
                                                            onValueChange(transactionDetails.copy(toAccountId = account.accountId.toString()))
                                                            onValueChange(transactionDetails.copy(payeeId = "-1"))
                                                            payeeExpanded = false
                                                        }
                                                    )
                                                }*/
                        }
                    }
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
                    value = transactionDetails.categoryId,
                    readOnly = true,
                    onValueChange = { onValueChange(transactionDetails.copy(categoryId = it)) },
                    label = { Text("Transaction Category *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    transactionUiState1.categoriesList.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.categName) },
                            onClick = {
                                onValueChange(transactionDetails.copy(categoryId = category.categId.toString()))
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = transactionDetails.transDate,
                onValueChange = { onValueChange(transactionDetails.copy(transactionNumber = it)) },
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = transactionDetails.transDate,
                onValueChange = { onValueChange(transactionDetails.copy(notes = it)) },
                label = { Text("Notes") }
            )

            OutlinedTextField(
                value = transactionDetails.transDate,
                onValueChange = { onValueChange(transactionDetails.copy(color = it)) },
                label = { Text("Color") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}