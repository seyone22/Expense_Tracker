package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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

@OptIn(ExperimentalMaterial3Api::class)
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
