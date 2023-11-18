package com.example.expensetracker.ui.account

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.AccountTypes
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

            Button(
                onClick = onEnterClick,
                enabled = accountUiState.isEntryValid,
                modifier = Modifier.fillMaxWidth()
            ) {
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
    var accountTypeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        OutlinedButton(
            onClick = { accountTypeExpanded = true }
        ) {
            Row {
                Text(text = accountDetails.accountType)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Description"
                )
            }
        }

        DropdownMenu(expanded = accountTypeExpanded , onDismissRequest = { accountTypeExpanded = false }) {
            enumValues<AccountTypes>().forEach { accountType ->
                val displayName: String = accountType.displayName
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onValueChange(accountDetails.copy(accountType = accountType.displayName))
                        accountTypeExpanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = accountDetails.accountName,
            onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
            label = { Text("Account Name *") }
        )

        OutlinedTextField(
            value = accountDetails.initialBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialBalance = it)) },
            label = { Text("Initial Balance *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = accountDetails.initialDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialDate = it)) },
            label = { Text("Opening Date  *") }
        )

        OutlinedTextField(
            value = accountDetails.notes.toString(),
            onValueChange = { onValueChange(accountDetails.copy(notes = it)) },
            label = { Text("Notes") }
        )

        Text(
            text = "Others",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = accountDetails.accountNum.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accountNum = it)) },
            label = { Text("Account Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = accountDetails.heldAt.toString(),
            onValueChange = { onValueChange(accountDetails.copy(heldAt = it)) },
            label = { Text("Held At") }
        )
        OutlinedTextField(
            value = accountDetails.website.toString(),
            onValueChange = { onValueChange(accountDetails.copy(website = it)) },
            label = { Text("Website") }
        )
        OutlinedTextField(
            value = accountDetails.contactInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(contactInfo = it)) },
            label = { Text("Contact Information") }
        )

        OutlinedTextField(
            value = accountDetails.accessInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accessInfo = it)) },
            label = { Text("Access Information") }
        )

        Text("Statement")

        Row(
            modifier = Modifier
        ) {
            Checkbox(
                checked = accountDetails.statementLocked.toBoolean(),
                onCheckedChange = { onValueChange(accountDetails.copy(statementLocked = it.toString())) },
            )
            Text(
                text = "Statement Locked",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(CenterVertically)
            )
        }
        OutlinedTextField(
            value = accountDetails.statementDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(statementDate = it)) },
            label = { Text("Statement Date") }
        )

        OutlinedTextField(
            value = accountDetails.minimumBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumBalance = it)) },
            label = { Text("Minimum Balance") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Text("Credit")

        OutlinedTextField(
            value = accountDetails.creditLimit.toString(),
            onValueChange = { onValueChange(accountDetails.copy(creditLimit = it)) },
            label = { Text("Credit Limit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)

        )
        OutlinedTextField(
            value = accountDetails.interestRate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(interestRate = it)) },
            label = { Text("Interest Rate") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = accountDetails.paymentDueDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(paymentDueDate = it)) },
            label = { Text("Payment Due Date") }
        )
        OutlinedTextField(
            value = accountDetails.minimumPayment.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumPayment = it)) },
            label = { Text("Minimum Payment") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountEntryFormPreview() {
    ExpenseTrackerTheme {
        AccountEntryForm(accountDetails = AccountDetails())
    }
}