package com.example.expensetracker.ui.transaction

import android.media.effect.Effect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                                onDismissRequest()
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
    transactionDetails: TransactionDetails,
    onValueChange: (TransactionDetails) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TransactionEntryViewModel,

    ) {
    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column {
        OutlinedTextField(
            value = transactionDetails.transDate,
            onValueChange = { onValueChange(transactionDetails.copy(transDate = it)) },
            label = { Text("Transaction Date") }
        )

        OutlinedButton(
            onClick = { statusExpanded = true }
        ) {
            Row {
                Text(text = transactionDetails.status)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Description"
                )
            }
        }

        DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
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

        OutlinedTextField(
            value = transactionDetails.transAmount,
            onValueChange = { onValueChange(transactionDetails.copy(transAmount = it)) },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedButton(
            onClick = { typeExpanded = true }
        ) {
            Row {
                Text(text = transactionDetails.transCode)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Description"
                )
            }
        }

        DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
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

        Row {
            when (transactionDetails.transCode) {
                TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                    Text(text = "Account")
                }

                TransactionCode.TRANSFER.displayName -> {
                    Text(text = "To Account")
                }
            }
            OutlinedButton(
                onClick = {
                    accountExpanded = true
                    coroutineScope.launch {
                        viewModel.getAllAccounts()
                    }

                }
            ) {
                Row {
                    Text(text = transactionDetails.accountId) //TODO : Find name for AccountId
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Description"
                    )
                }
            }
        }

        DropdownMenu(expanded = accountExpanded, onDismissRequest = { accountExpanded = false }) {
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

        Row {
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
            OutlinedButton(
                onClick = {
                    payeeExpanded = true
                    when (transactionDetails.transCode) {
                        TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                            coroutineScope.launch {
                                viewModel.getAllAccounts()
                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            coroutineScope.launch {

                            }
                        }
                    }
                }
            ) {
                Row {
                    Text(text = transactionDetails.payeeId) //TODO : Find name for PayeeID
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Description"
                    )
                }
            }
        }

        //TODO: This should populate from payee table, only account table when transfer
        DropdownMenu(expanded = payeeExpanded, onDismissRequest = { payeeExpanded = false }) {
            when (transactionDetails.transCode) {
                TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                    viewModel.transactionUiState.accountsList.forEach { account ->
                        val displayName: String = account.accountName
                        DropdownMenuItem(
                            text = { Text(displayName) },
                            onClick = {
                                onValueChange(transactionDetails.copy(payeeId = account.accountId.toString()))
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

        OutlinedButton(
            onClick = {
                categoryExpanded = true
                coroutineScope.launch {
                    viewModel.getAllAccounts()
                }

            }
        ) {
            Row {
                Text(text = transactionDetails.categoryId)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Description"
                )
            }
        }

        //TODO: This should populate from categories table
        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
            viewModel.transactionUiState.accountsList.forEach { account ->
                val displayName: String = account.accountName
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onValueChange(transactionDetails.copy(categoryId = account.accountId.toString()))
                        payeeExpanded = false
                    }
                )
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