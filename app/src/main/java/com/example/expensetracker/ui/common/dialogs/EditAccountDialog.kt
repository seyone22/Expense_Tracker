package com.example.expensetracker.ui.common.dialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.screen.operations.account.AccountEntryForm
import com.example.expensetracker.ui.screen.operations.account.toAccountDetails

@SuppressLint("UnrememberedMutableState")
@Composable
fun EditAccountDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedAccount: Account,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: ViewModel,
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val accountSelected by remember { mutableStateOf(selectedAccount.toAccountDetails()) }

    /*    viewModel.updateCurrencyState(
            viewModel.currencyUiState.currencyDetails.copy(
                currencyName = selectedCurrency.currencyName
            )
        )*/
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(1000.dp)
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
                LazyColumn() {
                    item {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )

                        AccountEntryForm(
                            accountDetails = accountSelected
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
}