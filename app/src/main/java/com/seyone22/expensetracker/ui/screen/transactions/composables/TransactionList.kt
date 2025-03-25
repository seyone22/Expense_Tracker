package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.TimeRangeFilter
import com.seyone22.expensetracker.ui.common.dialogs.DeleteItemDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.EditTransactionDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransactionDetails
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    entryViewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    showFilter: Boolean = true,
    useLazyColumn: Boolean = false, // Determines whether to use LazyColumn or Column
    count: Int? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val haptics = LocalHapticFeedback.current

    // Filter management
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()

    // Bottom sheet state management
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val transactionUiState by entryViewModel.transactionUiState.collectAsState()

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    // Handle bottom sheet actions
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
                    SortBar()
                }
            }
            if (filteredTransactions.isNotEmpty()) {
                items(filteredTransactions.size) { index ->
                    TransactionItem(
                        transaction = filteredTransactions[index],
                        haptics = haptics,
                        longClicked = { openBottomSheet(it) },
                        viewModel = viewModel,
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
                SortBar()
            }
            if (filteredTransactions.isNotEmpty()) {
                filteredTransactions.take(count ?: filteredTransactions.size)
                    .forEach { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            haptics = haptics,
                            longClicked = { openBottomSheet(it) },
                            viewModel = viewModel,
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

data class TransactionFilters(
    val timeFilter: TimeRangeFilter? = null,
    val typeFilter: TransactionCode? = null,
    val statusFilter: TransactionStatus? = null,
    val accountFilter: Account? = null,
    val categoryFilter: Category? = null,
    val payeeFilter: Payee? = null,
    val currencyFilter: CurrencyFormat? = null,
    val tagFilter: Tag? = null
)
