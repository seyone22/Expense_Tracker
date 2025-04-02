package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.TimeRangeFilter
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    showFilter: Boolean = true,
    useLazyColumn: Boolean = false, // Determines whether to use LazyColumn or Column
    count: Int? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val haptics = LocalHapticFeedback.current

    // Filter management
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()

    // Bottom sheet state management
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    // Handle bottom sheet actions
    fun openBottomSheet(transaction: TransactionWithDetails) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.setSelectedTransaction(transaction)
        isBottomSheetVisible = true
    }

    fun closeBottomSheet() {
        isBottomSheetVisible = false
    }

    if (useLazyColumn) {

        LazyColumn(
            modifier = modifier,
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between items
        ) {
            if (showFilter) {
                item { SortBar() }
            }

            val groupedTransactions = filteredTransactions.groupBy { it.transDate }

            groupedTransactions.forEach { (date, transactions) ->
                // Sticky Date Header
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        date?.let {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("MMMM d")), // Example: "March 1"
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Transactions under the date
                items(transactions.size) { idx ->
                    TransactionItem(
                        transaction = transactions[idx],
                        haptics = haptics,
                        longClicked = { openBottomSheet(transactions[idx]) },
                        viewModel = viewModel,
                        style = TransactionStyle.Status
                    )

                    // Add divider between transactions, but not after the last one
                    if (transactions[idx] != transactions.last()) {
                        HorizontalDivider()
                    }
                }
            }

            // Empty state
            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nothing to show here!",
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
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
                            style = TransactionStyle.Date
                        )
                        // Add divider between transactions, but not after the last one
                        if (transaction != filteredTransactions[(count ?: 1) - 1]) {
                            HorizontalDivider()
                        }
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
        ExpandableTransactionBottomSheet(
            isVisible = isBottomSheetVisible,
            closeBottomSheet = { closeBottomSheet() },
            viewModel = viewModel,
        )
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
