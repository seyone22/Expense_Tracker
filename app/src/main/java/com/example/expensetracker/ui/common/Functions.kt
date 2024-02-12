package com.example.expensetracker.ui.common

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryForm
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.toTransactionDetails
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FormattedCurrency(
    modifier: Modifier = Modifier,
    value : Double,
    currency : CurrencyFormat,
    type: TransactionType = TransactionType.NEUTRAL
) {
    if (currency.pfx_symbol != "") {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "${currency.pfx_symbol}${DecimalFormat("#.##").format(value)}",
            color = if(type == TransactionType.DEBIT) { MaterialTheme.colorScheme.error } else  { MaterialTheme.colorScheme.onBackground }
        )
    } else {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "${DecimalFormat("#.##").format(value)}${currency.sfx_symbol}",
            color = if(type == TransactionType.DEBIT) { MaterialTheme.colorScheme.error } else  { MaterialTheme.colorScheme.onBackground }
        )
    }
}

fun removeTrPrefix(input: String): String {
    val prefix = "_tr_"

    return if (input.startsWith(prefix)) {
        input.removePrefix(prefix)
    } else {
        input
    }
}

fun getAbbreviatedMonthName(monthValue: Int, locale: Locale = Locale.getDefault()): String {
    val month = Month.of(monthValue)
    return month.getDisplayName(TextStyle.SHORT, locale)
}

enum class TransactionType {
    DEBIT,
    CREDIT,
    NEUTRAL
}


enum class EntryFields {
    STATUS,
    TYPE,
    ACCOUNT,
    PAYEE,
    CATEGORY,
    TRANSACTION
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun TransactionEditDialog(
    modifier: Modifier = Modifier,
    title: String = "Edit Transaction",
    selectedTransaction: Transaction,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    edit: Boolean = false
) {
    val viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val transactionSelected by remember { mutableStateOf(selectedTransaction) }

    /*    viewModel.updateCurrencyState(
            viewModel.currencyUiState.currencyDetails.copy(
                currencyName = selectedCurrency.currencyName
            )
        )*/
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(900.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                TransactionEntryForm(
                    transactionDetails = transactionSelected.toTransactionDetails(),
                    viewModel = viewModel,
                    coroutineScope = coroutineScope
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirmClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = true
                    ) {
                        Text("Confirm")
                    }
                }


            }
        }
    }
}

