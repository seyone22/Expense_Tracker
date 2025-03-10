package com.seyone22.expensetracker.ui.common.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.operations.transaction.BillsDepositsDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.composables.TransactionEntryForm

@SuppressLint("UnrememberedMutableState")
@Composable
fun EditTransactionDialog(
    modifier: Modifier = Modifier,
    title: String = "Edit Transaction",
    selectedTransaction: TransactionDetails,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    edit: Boolean = false
) {
    val viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val transactionUiState by viewModel.transactionUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    var transactionSelected by remember { mutableStateOf(selectedTransaction) }

    viewModel.updateUiState(
        transactionUiState.transactionDetails.copy(
            transId = transactionSelected.transId,
            transDate = transactionSelected.transDate,
            status = transactionSelected.status,
            toTransAmount = transactionSelected.toTransAmount,
            transAmount = transactionSelected.transAmount,
            transCode = transactionSelected.transCode,
            toAccountId = transactionSelected.toAccountId,
            payeeId = transactionSelected.payeeId,
            accountId = transactionSelected.accountId,
            categoryId = transactionSelected.categoryId,
            transactionNumber = transactionSelected.transactionNumber,
            notes = transactionSelected.notes,
            color = transactionSelected.color
        ),
        billsDepositsDetails = BillsDepositsDetails()
    )

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TransactionEntryForm(
                        transactionDetails = transactionSelected,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        onValueChange = { transactionDetails, _ ->
                            transactionSelected = transactionDetails
                        },
                        edit = true,
                        transactionUiState = transactionUiState
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
}