package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.EntryFields
import com.example.expensetracker.ui.common.TAG
import com.example.expensetracker.ui.common.TransactionEditDialog
import com.example.expensetracker.ui.common.dialogs.EditAccountDialog
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.transactions.TransactionList
import kotlinx.coroutines.launch
import kotlin.math.log

object AccountDetailDestination : NavigationDestination {
    override val route = "AccountDetails"
    override val titleRes = R.string.app_name
    override val routeId = 13
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    backStackEntry: String,
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    viewModel.accountId = backStackEntry.toInt()

    val accountDetailAccountUiState by viewModel.accountDetailAccountUiState.collectAsState()
    val accountDetailTransactionUiState by viewModel.accountDetailTransactionUiState.collectAsState()

    var isSelected by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf(Transaction()) }
    val openEditDialog = remember { mutableStateOf(false) }
    val openEditType = remember { mutableStateOf(EntryFields.ACCOUNT) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getTransactions()
        viewModel.getAccount()
    }

    Column(
        modifier = modifier
            .padding(0.dp, 100.dp)
    ) {
        DetailedAccountCard(
            modifier = modifier,
            accountDetailAccountUiState = accountDetailAccountUiState,
            setOpenEditDialog = { it -> openEditDialog.value = it },
            setOpenEditDialogType = { it -> openEditType.value = it }
        )

        TransactionList(
            transactions = accountDetailTransactionUiState.transactions,
            modifier = modifier,
            longClicked = { selected ->
                isSelected = !isSelected
                selectedTransaction = selected
            },
        )

        if (openEditDialog.value and (openEditType.value == EntryFields.TRANSACTION)) {
            TransactionEditDialog(
                onConfirmClick = {
                    coroutineScope.launch {
                        viewModel.editTransaction()
                    }
                },
                onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                edit = true,
                title = "Edit Transaction",
                selectedTransaction = selectedTransaction
            )
        }
        if (openEditDialog.value and (openEditType.value == EntryFields.ACCOUNT)) {
            EditAccountDialog(
                onConfirmClick = {
                    coroutineScope.launch {
                        viewModel.editTransaction()
                    }
                },
                onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                viewModel = viewModel,
                edit = true,
                title = "Edit Account",
                selectedAccount = accountDetailAccountUiState.account
            )
        }
    }
}

@Composable
fun DetailedAccountCard(
    modifier: Modifier,
    accountDetailAccountUiState: AccountDetailAccountUiState,
    setOpenEditDialog: (Boolean) -> Unit,
    setOpenEditDialogType: (EntryFields) -> Unit,
) {
    Log.d(TAG, "DetailedAccountCard: $accountDetailAccountUiState")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp)
    ) {
        Row(
            modifier = modifier
                .padding(16.dp, 8.dp, 0.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.width(300.dp)
            ) {
                Column {
                    Text(
                        text = accountDetailAccountUiState.account.accountName,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = accountDetailAccountUiState.account.accountType + " Account",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                HorizontalDivider()
                Text(
                    text = "Account Balance : " +
                            (accountDetailAccountUiState.account.initialBalance?.plus(accountDetailAccountUiState.balance)).toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Reconciled Balance : " + (accountDetailAccountUiState.account.initialBalance?.plus(
                        accountDetailAccountUiState.balance
                    )).toString(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            IconButton(onClick = {
                setOpenEditDialog(true)
                setOpenEditDialogType(EntryFields.ACCOUNT)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    Modifier.size(24.dp, 24.dp)
                )
            }
        }
    }
}