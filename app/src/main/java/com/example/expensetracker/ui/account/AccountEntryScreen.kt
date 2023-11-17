package com.example.expensetracker.ui.account

import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.expensetracker.AccountTypes
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch

object AccountEntryDestination : NavigationDestination {
    override val route = "EnterAccount"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: AccountEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    AccountEntryBody(
        onEnterClick = {
            coroutineScope.launch {
                viewModel.saveAccount()
                navigateBack()
            }
        },
        accountUiState = viewModel.accountUiState,
        onAccountValueChange = viewModel::updateUiState
    )
}

@Composable
fun AccountEntryBody(
    onEnterClick: () -> Unit,
    accountUiState: AccountUiState,
    onAccountValueChange: (AccountDetails) -> Unit,
) {
    LazyColumn {
        item {
            AccountEntryForm(
                accountDetails = accountUiState.accountDetails,
                onValueChange = onAccountValueChange,
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = onEnterClick) {
                Text("Create")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEntryForm(
    accountDetails: AccountDetails,
    onValueChange: (AccountDetails) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
    ) {

        OutlinedButton(onClick = { expanded = true }) {
            Text(text = accountDetails.accountType)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            enumValues<AccountTypes>().forEach { accountType ->
                val displayName: String = accountType.displayName
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = accountDetails.accountName,
            onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
            label = { Text("Account Name") }
        )

        OutlinedTextField(
            value = accountDetails.initialBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialBalance = it)) },
            label = { Text("Initial Balance") }
        )

        OutlinedTextField(
            value = accountDetails.initialDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialDate = it)) },
            label = { Text("Opening Date") }
        )

        OutlinedButton(onClick = { expanded = true }) {
            Text(text = accountDetails.accountType)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            enumValues<AccountTypes>().forEach { accountType ->
                val displayName: String = accountType.displayName
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = accountDetails.notes.toString(),
            onValueChange = { onValueChange(accountDetails.copy(notes = it)) },
            label = { Text("Notes") }
        )

        Text("Others")

        OutlinedTextField(
            value = accountDetails.accountNum.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accountNum = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.heldAt.toString(),
            onValueChange = { onValueChange(accountDetails.copy(heldAt = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.website.toString(),
            onValueChange = { onValueChange(accountDetails.copy(website = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.contactInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(contactInfo = it)) },
            label = { Text("Notes") }
        )

        OutlinedTextField(
            value = accountDetails.accessInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accessInfo = it)) },
            label = { Text("Notes") }
        )

        Text("Statement")

        OutlinedTextField(
            value = accountDetails.statementLocked.toString(),
            onValueChange = { onValueChange(accountDetails.copy(statementLocked = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.statementDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(statementDate = it)) },
            label = { Text("Notes") }
        )

        OutlinedTextField(
            value = accountDetails.minimumBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumBalance = it)) },
            label = { Text("Notes") }
        )

        Text("Credit")

        OutlinedTextField(
            value = accountDetails.creditLimit.toString(),
            onValueChange = { onValueChange(accountDetails.copy(creditLimit = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.interestRate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(interestRate = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.paymentDueDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(paymentDueDate = it)) },
            label = { Text("Notes") }
        )
        OutlinedTextField(
            value = accountDetails.minimumPayment.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumPayment = it)) },
            label = { Text("Notes") }
        )
    }
}