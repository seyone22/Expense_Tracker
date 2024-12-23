package com.example.expensetracker.ui.screen.transactions

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.data.model.BillsDepositWithDetails
import com.example.expensetracker.data.model.BillsDeposits
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionWithDetails
import com.example.expensetracker.data.model.toBillsDeposit
import com.example.expensetracker.data.model.toTransaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.SortBar
import com.example.expensetracker.ui.common.TransactionType
import com.example.expensetracker.ui.common.getAbbreviatedMonthName
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.transactions.composables.TransactionList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TransactionsDestination : NavigationDestination {
    override val route = "Entries"
    override val titleRes = R.string.app_name
    override val routeId = 3
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    setTopBarAction: (Int) -> Unit,
    setIsItemSelected: (Boolean) -> Unit,
    setSelectedObject: (SelectedObjects) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var state by remember { mutableIntStateOf(0) }
    setTopBarAction(8)

    val titles = listOf("Transactions", "Scheduled")

    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = state,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        setTopBarAction(state)
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxHeight()

        ) { page ->
            when (page) {
                0 -> {
                    state = pagerState.currentPage
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

                1 -> {
                    state = pagerState.currentPage
                    ScheduledTransactionList(
                        billsDeposits = transactionsUiState.billsDeposits,
                        longClicked = { selected ->
                            setIsItemSelected(true)
                            val selObj = SelectedObjects(billsDeposits = selected)
                            setSelectedObject(selObj)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduledTransactionList(
    billsDeposits: List<BillsDepositWithDetails>,
    longClicked: (BillsDeposits) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val haptics = LocalHapticFeedback.current
    var filteredTransactions by remember { mutableStateOf(billsDeposits) }
    // Use derivedStateOf to update filteredTransactions when transactions change
    val derivedFilteredTransactions by remember(billsDeposits) {
        derivedStateOf {
            billsDeposits // or apply your filtering logic here
        }
    }
    filteredTransactions = derivedFilteredTransactions

    Column {
        SortBar(
            periodSortAction = { sortCase ->
                filteredTransactions = when (sortCase) {
                    0 -> billsDeposits
                    1 -> billsDeposits.filter {
                        val transactionDate =
                            LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
                        transactionDate.monthValue == LocalDate.now().monthValue
                    }

                    3 -> billsDeposits.filter {
                        val transactionDate =
                            LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
                        // Check if the transaction date is in the last month
                        when {
                            LocalDate.now().monthValue != 1 ->
                                transactionDate.monthValue == LocalDate.now().monthValue - 1

                            else ->
                                transactionDate.year == LocalDate.now().year - 1 && transactionDate.monthValue == 12
                        }
                    }
                    // Add more cases as needed
                    else -> billsDeposits // No filtering for other cases
                }
            }
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
                                        ?.let { it1 -> viewModel.getCurrencyFormatById(it1.currencyId) }
                                currencyFormat = currencyFormatFunction!!
                            }

                            FormattedCurrency(
                                value = filteredTransactions[it].TRANSAMOUNT,
                                currency = currencyFormat,
                                type = if ((filteredTransactions[it].TRANSCODE == "Deposit") or (filteredTransactions[it].TOACCOUNTID != -1)) {
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
                                longClicked(filteredTransactions[it].toBillsDeposit())
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
}