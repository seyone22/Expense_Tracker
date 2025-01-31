package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.toBillsDeposit
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FilterOption
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    FilterOption.ALL -> billsDeposits
                    FilterOption.CURRENT_MONTH -> billsDeposits.filter {
                        val transactionDate =
                            LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
                        transactionDate.monthValue == LocalDate.now().monthValue
                    }

                    FilterOption.LAST_MONTH -> billsDeposits.filter {
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