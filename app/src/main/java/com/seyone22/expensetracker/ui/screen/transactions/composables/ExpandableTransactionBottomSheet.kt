package com.seyone22.expensetracker.ui.screen.transactions.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.ProfileAvatarWithFallback
import com.seyone22.expensetracker.ui.common.dialogs.DeleteItemDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.EditTransactionDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.MoveTransactionDialogAction
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransactionDetails
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableTransactionBottomSheet(
    isVisible: Boolean,
    closeBottomSheet: () -> Unit,
    viewModel: TransactionsViewModel,
    entryViewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val sheetVisibility by remember { mutableStateOf(isVisible) }

    val transaction = viewModel.selectedTransaction.collectAsState()
    var currency by remember { mutableStateOf<CurrencyFormat?>(CurrencyFormat()) }

    LaunchedEffect(transaction) {
        currency = sharedViewModel.getCurrencyForAccount(transaction.value!!.accountId)
    }

    if (!sheetVisibility) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Prevents Compose from entering an unstable partial state
    )
    val coroutineScope = rememberCoroutineScope()

    // Ensure the bottom sheet expands fully when shown

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                closeBottomSheet()
            }
        },
        sheetState = sheetState,
    ) {
        if (transaction.value != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    val letter: String = transaction.value!!.payeeName?.get(0).toString()
                    letter.uppercase(
                        Locale.ROOT
                    ).let {
                        ProfileAvatarWithFallback(
                            size = 77.dp,
                            fontSize = 33.sp,
                            initial = it,
                        )
                    }

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        FormattedCurrency(
                            value = transaction.value!!.transAmount,
                            currency = currency ?: CurrencyFormat(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        transaction.value?.let {
                            var accountName by remember { mutableStateOf("") }
                            coroutineScope.launch {
                                accountName =
                                    viewModel.getAccountFromId(it.accountId)?.accountName ?: ""
                            }
                            Text("${accountName} | ${it.status}")
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            transaction.value?.let {
                                item {
                                    SuggestionChip(
                                        enabled = true,
                                        onClick = {},
                                        label = { Text(text = "${it.transactionNumber}") })
                                }
                                item {
                                    SuggestionChip(
                                        enabled = true,
                                        onClick = {},
                                        label = { Text(text = "${it.categName}") })
                                }
                                item {
                                    SuggestionChip(
                                        enabled = true,
                                        onClick = {},
                                        label = { Text(text = it.transCode) })
                                }
                            }
                        }
                        if (transaction.value!!.notes != "") {
                            Text(
                                text = transaction.value!!.notes ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
        Column {
            HorizontalDivider()
            // Actions (Shown in Expanded State)
            SheetActionItem(Icons.Default.Edit, "Edit", {
                transaction.value?.let { transaction ->
                    viewModel.showDialog(
                        EditTransactionDialogAction(
                            onEdit = { transactionDetails ->
                                coroutineScope.launch {
                                    viewModel.editTransaction(transactionDetails)
                                }
                            },
                            initialTransaction = transaction.toTransaction().toTransactionDetails(),
                            viewModel = entryViewModel,
                            coroutineScope = coroutineScope,
                        )
                    )
                }
                closeBottomSheet()
            })
            SheetActionItem(Icons.Default.Check, "Reconcile transaction", {
                transaction.value?.let {
                    viewModel.setTransactionStatus(it.toTransaction(), TransactionStatus.R)
                    viewModel.updateSelectedTransaction()
                }
            })
            SheetActionItem(Icons.Default.ContentCopy, "Duplicate", {
                transaction.value?.let {
                    viewModel.duplicateTransaction(it.toTransaction())
                }
                closeBottomSheet()
            })
            SheetActionItem(
                Icons.AutoMirrored.Filled.ArrowForward, "Move to...", {
                    transaction.value?.let {
                        viewModel.showDialog(
                            MoveTransactionDialogAction(
                                onAdd = { newAccountId ->
                                    viewModel.moveTransaction(it, newAccountId)
                                })
                        )
                    }
                    closeBottomSheet()
                })
            SheetActionItem(Icons.Default.Share, "Share", {
                closeBottomSheet()
            })
            SheetActionItem(Icons.Default.Delete, "Delete", {
                transaction.value?.let { transaction ->
                    coroutineScope.launch {
                        viewModel.showDialog(
                            DeleteItemDialogAction(
                                onAdd = {
                                    viewModel.deleteTransaction(transaction.toTransaction())
                                }, itemName = "this transaction"
                            )
                        )
                    }
                }
                closeBottomSheet()
            }, Color.Red)
        }

    }
}

@Composable
fun SheetActionItem(
    icon: ImageVector, text: String, action: () -> Unit, tint: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { action() }
            .padding(start = 22.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = text, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = tint)
    }
}
