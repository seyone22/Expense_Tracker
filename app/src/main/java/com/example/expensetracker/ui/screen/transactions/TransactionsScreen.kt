package com.example.expensetracker.ui.screen.transactions

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionWithDetails
import com.example.expensetracker.model.toTransaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.SortBar
import com.example.expensetracker.ui.common.TransactionType
import com.example.expensetracker.ui.common.getAbbreviatedMonthName
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TransactionsDestination : NavigationDestination {
    override val route = "Entries"
    override val titleRes = R.string.app_name
    override val routeId = 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    setTopBarAction : (Int) -> Unit,
    setIsItemSelected: (Boolean) -> Unit,
    setSelectedObject : (SelectedObjects) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()
    setTopBarAction(8)

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Column() {
        TransactionList(
            transactions = transactionsUiState.transactions,
            longClicked = { selected ->
                setIsItemSelected(true)
                val selObj = SelectedObjects(transaction = selected)
                Log.d("TAG", "TransactionsScreen: $selObj")
                setSelectedObject(selObj)
            },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    transactions: List<TransactionWithDetails>,
    longClicked: (Transaction) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val haptics = LocalHapticFeedback.current
    var filteredTransactions by remember { mutableStateOf(transactions) }
    // Use derivedStateOf to update filteredTransactions when transactions change
    val derivedFilteredTransactions by remember(transactions) {
        derivedStateOf {
            transactions // or apply your filtering logic here
        }
    }
    filteredTransactions = derivedFilteredTransactions

    SortBar(
        periodSortAction = { sortCase ->
            filteredTransactions = when (sortCase) {
                0 -> transactions
                1 -> transactions.filter {
                    val transactionDate =
                        LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    transactionDate.monthValue == LocalDate.now().monthValue
                }

                3 -> transactions.filter {
                    val transactionDate =
                        LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    // Check if the transaction date is in the last month
                    when {
                        LocalDate.now().monthValue != 1 ->
                            transactionDate.monthValue == LocalDate.now().monthValue - 1

                        else ->
                            transactionDate.year == LocalDate.now().year - 1 && transactionDate.monthValue == 12
                    }
                }
                // Add more cases as needed
                else -> transactions // No filtering for other cases
            }
        }
    )

    LazyColumn(
        modifier = modifier
    ) {
        items(count = filteredTransactions.size) {
            ListItem(
                headlineContent = {
                    Text(text = filteredTransactions[it].payeeName)
                },
                supportingContent = {
                    Text(text = removeTrPrefix(filteredTransactions[it].categName))
                },
                trailingContent = {
                    var currencyFormat by remember { mutableStateOf(CurrencyFormat()) }

                    LaunchedEffect(filteredTransactions[it].accountId) {
                        val currencyFormatFunction =
                            viewModel.getAccountFromId(filteredTransactions[it].accountId)
                                ?.let { it1 -> viewModel.getCurrencyFormatById(it1.currencyId) }
                        currencyFormat = currencyFormatFunction!!
                    }

                    FormattedCurrency(
                        value = filteredTransactions[it].transAmount,
                        currency = currencyFormat,
                        type = if (filteredTransactions[it].transCode == "Deposit") {
                            TransactionType.CREDIT
                        } else {
                            TransactionType.DEBIT
                        }
                    )
                },
                leadingContent = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getAbbreviatedMonthName(
                                filteredTransactions[it].transDate!!.substring(
                                    5,
                                    7
                                ).toInt()
                            )
                        )
                        Text(text = filteredTransactions[it].transDate!!.substring(8, 10))
                    }
                },
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(filteredTransactions[it].toTransaction())
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()
        }
    }
}