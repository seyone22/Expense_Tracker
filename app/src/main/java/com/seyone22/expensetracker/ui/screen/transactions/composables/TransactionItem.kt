package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.getAbbreviatedMonthName
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    transaction: TransactionWithDetails,
    haptics: HapticFeedback,
    longClicked: (Transaction) -> Unit,
    viewModel: TransactionsViewModel,
    forAccountId: Int
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)


    var toAccount: Account? by remember { mutableStateOf(null) }
    var currencyFormat: CurrencyFormat? by remember { mutableStateOf(null) }

    if (transaction.transCode == TransactionCode.TRANSFER.displayName && forAccountId != transaction.accountId) {
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

    ListItem(
        headlineContent = {
            Text(
                text = transaction.payeeName ?: "Transfer -> ${toAccount?.accountName}"
            )
        },
        supportingContent = { Text(text = removeTrPrefix(transaction.categName)) },
        trailingContent = {

            FormattedCurrency(
                value = if (transaction.transCode == TransactionCode.TRANSFER.displayName && forAccountId != transaction.accountId) transaction.toTransAmount
                    ?: 0.0 else transaction.transAmount,
                currency = currencyFormat ?: CurrencyFormat(),
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