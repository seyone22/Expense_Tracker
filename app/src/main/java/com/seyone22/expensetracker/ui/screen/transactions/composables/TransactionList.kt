package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.utils.filterTransactions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    transactions: List<TransactionWithDetails>,
    longClicked: (Transaction) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    showFilter: Boolean = true
) {
    val haptics = LocalHapticFeedback.current
    var filteredTransactions by remember { mutableStateOf(transactions) }

    // Use derivedStateOf to update filteredTransactions when transactions change
    val derivedFilteredTransactions by remember(transactions) {
        derivedStateOf {
            transactions // Apply your filtering logic here if needed
        }
    }
    LaunchedEffect(transactions) {
        filteredTransactions = transactions
    }

    Column(modifier = modifier) {
        if (showFilter) {
            SortBar(
                periodSortAction = { sortCase ->
                    filteredTransactions = filterTransactions(
                        transactions = transactions,
                        filterOption = sortCase
                    )
                }
            )
        }

        if (filteredTransactions.isNotEmpty()) {
            // Iterate over the list of filtered transactions
            filteredTransactions.forEachIndexed { index, transaction ->
                ListItem(
                    headlineContent = {
                        Text(text = transaction.payeeName ?: "Transfer")
                    },
                    supportingContent = {
                        Text(text = removeTrPrefix(transaction.categName))
                    },
                    trailingContent = {
                        var currencyFormat by remember { mutableStateOf(CurrencyFormat()) }

                        LaunchedEffect(transaction.accountId) {
                            val currencyFormatFunction =
                                viewModel.getAccountFromId(transaction.accountId)
                                    ?.let { it1 -> viewModel.getCurrencyFormatById(it1.currencyId) }
                            currencyFormat = currencyFormatFunction!!
                        }

                        FormattedCurrency(
                            value = transaction.transAmount,
                            currency = currencyFormat,
                            type = if ((transaction.transCode == "Deposit") || (transaction.toAccountId != -1)) {
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
                                    transaction.transDate!!.substring(5, 7).toInt()
                                )
                            )
                            Text(text = transaction.transDate.substring(8, 10))
                        }
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            longClicked(transaction.toTransaction())
                        },
                        onLongClickLabel = " "
                    )
                )
                HorizontalDivider()
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
