package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.data.model.toBillsDeposit
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.TimeRangeFilter
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.SortOption
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.dialogs.DeleteItemDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.EditTransactionDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.utils.filterBillDeposits
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScheduledTransactionList(
    longClicked: (BillsDeposits) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val entryViewModel: TransactionEntryViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val transactionUiState by entryViewModel.transactionUiState.collectAsState()
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()

    val haptics = LocalHapticFeedback.current

    var selectedTimeFilter by remember { mutableStateOf<TimeRangeFilter?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<TransactionCode?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<TransactionStatus?>(null) }
    var selectedAccountFilter by remember { mutableStateOf<Account?>(null) }
    var selectedPayeeFilter by remember { mutableStateOf<Payee?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) }

    var selectedSort by remember { mutableStateOf<SortOption>(SortOption.default) }

    val filteredTransactions =
        remember(
            transactionsUiState.billsDeposits,
            selectedTimeFilter,
            selectedTypeFilter,
            selectedStatusFilter,
            selectedAccountFilter,
            selectedSort
        ) {
            filterBillDeposits(
                transactionsUiState.billsDeposits,
                selectedTimeFilter,
                selectedTypeFilter,
                selectedStatusFilter,
                selectedPayeeFilter,
                selectedCategoryFilter,
                selectedAccountFilter,
            )
        }

    var selectedTransaction by remember { mutableStateOf<BillsDeposits?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    fun openBottomSheet(transaction: BillsDeposits) {
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
                    initialTransaction = TransactionDetails(),
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
                                viewModel.deleteBillDeposit(transaction)
                            }
                        }, itemName = "this transaction"
                    )
                )
            }
        }
        closeBottomSheet()
    }

    Column {
        SortBar(
        )

        if (filteredTransactions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
            ) {
                items(count = filteredTransactions.size) {
                    ListItem(
                        headlineContent = {
                            Text(text = filteredTransactions[it].payeeName ?: "Transfer")
                        },
                        supportingContent = {
                            Text(text = removeTrPrefix(filteredTransactions[it].categName))
                        },
                        trailingContent = {
                            var currencyFormat by remember { mutableStateOf(CurrencyFormat()) }

                            LaunchedEffect(filteredTransactions[it].ACCOUNTID) {
                                val currencyFormatFunction =
                                    viewModel.getAccountFromId(filteredTransactions[it].ACCOUNTID)
                                        ?.let { it1 -> sharedViewModel.getCurrencyById(it1.currencyId) }
                                currencyFormat = currencyFormatFunction!!
                            }

                            FormattedCurrency(
                                value = filteredTransactions[it].TRANSAMOUNT,
                                currency = currencyFormat,
                                type = if ((filteredTransactions[it].TRANSCODE == "Deposit") or (filteredTransactions[it].TOACCOUNTID != -1)) {
                                    TransactionType.CREDIT
                                } else {
                                    TransactionType.DEBIT
                                },
                                defaultColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        leadingContent = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = getAbbreviatedMonthName(
                                        filteredTransactions[it].TRANSDATE!!.substring(
                                            5,
                                            7
                                        ).toInt()
                                    )
                                )
                                Text(text = filteredTransactions[it].TRANSDATE!!.substring(8, 10))
                            }
                        },
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                openBottomSheet(filteredTransactions[it].toBillsDeposit())
                            },
                            onLongClickLabel = "  "
                        )
                    )
                    HorizontalDivider()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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
                    Icon(Icons.Default.SkipNext, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Skip next transaction", style = MaterialTheme.typography.bodyLarge)
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editTransaction() }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.KeyboardDoubleArrowDown, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Enter next transaction now", style = MaterialTheme.typography.bodyLarge)
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