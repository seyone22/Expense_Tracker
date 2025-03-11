package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.dialogs.DeleteItemDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.EditTransactionDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransactionDetails
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.utils.filterTransactions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    transactions: List<TransactionWithDetails>,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    showFilter: Boolean = true,
    useLazyColumn: Boolean = false // Determines whether to use LazyColumn or Column
) {
    val haptics = LocalHapticFeedback.current
    var filteredTransactions by remember { mutableStateOf(transactions) }

    LaunchedEffect(transactions) {
        filteredTransactions = transactions
    }

    val coroutineScope = rememberCoroutineScope()
    val entryViewModel: TransactionEntryViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val transactionUiState by entryViewModel.transactionUiState.collectAsState()

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    fun openBottomSheet(transaction: Transaction) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        selectedTransaction = transaction
        isBottomSheetVisible = true
    }

    fun closeBottomSheet() {
        isBottomSheetVisible = false
    }

    fun editTransaction() {
        selectedTransaction?.let { transaction ->
            viewModel.showDialog(
                EditTransactionDialogAction(
                    onEdit = { transactionDetails ->
                        coroutineScope.launch {
                            viewModel.editTransaction(transactionDetails)
                        }
                    },
                    initialTransaction = transaction.toTransactionDetails(),
                    viewModel = entryViewModel,
                    coroutineScope = coroutineScope,
                    transactionUiState = transactionUiState
                )
            )
        }
        closeBottomSheet()
    }

    fun deleteTransaction() {
        selectedTransaction?.let { transaction ->
            coroutineScope.launch {
                viewModel.showDialog(
                    DeleteItemDialogAction(
                        onAdd = {
                            coroutineScope.launch {
                                viewModel.deleteTransaction(transaction)
                            }
                        }, itemName = "this transaction"
                    )
                )
            }
        }
        closeBottomSheet()
    }

    if (useLazyColumn) {
        LazyColumn(modifier = modifier) {
            if (showFilter) {
                item {
                    SortBar { sortCase ->
                        filteredTransactions = filterTransactions(transactions, sortCase)
                    }
                }
            }
            if (filteredTransactions.isNotEmpty()) {
                items(filteredTransactions.size) { index ->
                    TransactionItem(
                        transaction = filteredTransactions[index],
                        haptics = haptics,
                        longClicked = { openBottomSheet(it) },
                        viewModel = viewModel
                    )
                    HorizontalDivider()
                }
            } else {
                item {
                    Text(
                        text = "Nothing to show here!",
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        modifier = Modifier
                    )
                }
            }
        }
    } else {
        Column(modifier = modifier) {
            if (showFilter) {
                SortBar { sortCase ->
                    filteredTransactions = filterTransactions(transactions, sortCase)
                }
            }
            if (filteredTransactions.isNotEmpty()) {
                filteredTransactions.forEach { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        haptics = haptics,
                        longClicked = { openBottomSheet(it) },
                        viewModel = viewModel
                    )
                    HorizontalDivider()
                }
            } else {
                Text(
                    text = "Nothing to show here!",
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { closeBottomSheet() }, sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                HorizontalDivider()

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Edit", style = MaterialTheme.typography.bodyLarge)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Reconcile transaction", style = MaterialTheme.typography.bodyLarge)
                }


                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Duplicate", style = MaterialTheme.typography.bodyLarge)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Move to...", style = MaterialTheme.typography.bodyLarge)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Share", style = MaterialTheme.typography.bodyLarge)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { deleteTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Delete", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionItem(
    transaction: TransactionWithDetails,
    haptics: HapticFeedback,
    longClicked: (Transaction) -> Unit,
    viewModel: TransactionsViewModel
) {
    ListItem(headlineContent = { Text(text = transaction.payeeName ?: "Transfer") },
        supportingContent = { Text(text = removeTrPrefix(transaction.categName)) },
        trailingContent = {
            var currencyFormat by remember { mutableStateOf(CurrencyFormat()) }

            LaunchedEffect(transaction.accountId) {
                currencyFormat = viewModel.getAccountFromId(transaction.accountId)
                    ?.let { viewModel.getCurrencyFormatById(it.currencyId) } ?: CurrencyFormat()
            }

            FormattedCurrency(
                value = transaction.transAmount,
                currency = currencyFormat,
                type = if (transaction.transCode == "Deposit" || transaction.toAccountId != -1) TransactionType.CREDIT else TransactionType.DEBIT
            )
        },
        leadingContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = getAbbreviatedMonthName(
                        transaction.transDate!!.substring(5, 7).toInt()
                    )
                )
                Text(text = transaction.transDate.substring(8, 10))
            }
        },
        modifier = Modifier.combinedClickable(onClick = {}, onLongClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            longClicked(transaction.toTransaction())
        }, onLongClickLabel = " "
        )
    )
}
