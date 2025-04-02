package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel

enum class TransactionStyle {
    Date, Status
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    transaction: TransactionWithDetails,
    haptics: HapticFeedback,
    longClicked: (TransactionWithDetails) -> Unit,
    viewModel: TransactionsViewModel,
    style: TransactionStyle = TransactionStyle.Date
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val filters by viewModel.filters.collectAsState()
    var toAccount: Account? by remember { mutableStateOf(null) }
    var currencyFormat: CurrencyFormat? by remember { mutableStateOf(null) }

    val clarifiedName by produceState(initialValue = "") {
        value = viewModel.getClarifiedName(transaction.categoryId ?: -1)
    }

    if (transaction.transCode == TransactionCode.TRANSFER.displayName && filters.accountFilter?.accountId != transaction.accountId) {
        LaunchedEffect(transaction.toAccountId) {
            toAccount = viewModel.getAccountFromId(transaction.toAccountId ?: 0)

            currencyFormat = sharedViewModel.getCurrencyById(toAccount?.currencyId ?: 0)

        }
    } else {
        LaunchedEffect(transaction.accountId) {
            currencyFormat = viewModel.getAccountFromId(transaction.accountId)
                ?.let { sharedViewModel.getCurrencyById(it.currencyId) } ?: CurrencyFormat()
        }
    }

    ListItem(headlineContent = {
        Text(
            text = transaction.payeeName ?: "Transfer -> ${toAccount?.accountName}"
        )
    }, supportingContent = { Text(text = clarifiedName) }, trailingContent = {
        FormattedCurrency(
            value = if (transaction.transCode == TransactionCode.TRANSFER.displayName && filters.accountFilter?.accountId != transaction.accountId) transaction.toTransAmount
                ?: 0.0 else transaction.transAmount,
            currency = currencyFormat ?: CurrencyFormat(),
            type = if (transaction.transCode == "Deposit" || transaction.toAccountId != -1) TransactionType.CREDIT else TransactionType.DEBIT
        )
    }, leadingContent = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (style == TransactionStyle.Date) {
                Text(
                    text = getAbbreviatedMonthName(
                        transaction.transDate!!.substring(5, 7).toInt()
                    )
                )
                Text(text = transaction.transDate.substring(8, 10))
            } else {

                Text(
                    text = (transaction.status ?: "").substring(0, 1),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }, modifier = Modifier.combinedClickable(onClick = {}, onLongClick = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        longClicked(transaction)
    }, onLongClickLabel = " "
    )
    )
}