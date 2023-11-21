package com.example.expensetracker.ui.account

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: AccountEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                        coroutineScope.launch {
                            viewModel.saveAccount()
                            navigateBack()
                        }
                        },
                        modifier = modifier.padding(0.dp,0.dp,8.dp,0.dp)
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { padding ->
        AccountEntryBody(
            accountUiState = viewModel.accountUiState,
            onAccountValueChange = viewModel::updateUiState,
            modifier = modifier.padding(padding)
        )
    }

}

@Composable
fun AccountEntryBody(
    accountUiState: AccountUiState = AccountUiState(),
    onAccountValueChange: (AccountDetails) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            AccountEntryForm(
                accountDetails = accountUiState.accountDetails,
                onValueChange = onAccountValueChange,
                modifier = Modifier
            )
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
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .focusGroup()
            .padding(0.dp, 8.dp)
    )
    {
        ExposedDropdownMenuBox(
            expanded = accountTypeExpanded,
            onExpandedChange = { accountTypeExpanded = !accountTypeExpanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .clickable(enabled = true) { accountTypeExpanded = true }
                    .menuAnchor(),
                value = accountDetails.accountType,
                readOnly = true,
                onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
                label = { Text("Account Name *") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) }),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountTypeExpanded) },
            )

            ExposedDropdownMenu(
                expanded = accountTypeExpanded,
                onDismissRequest = { accountTypeExpanded = false },
            ) {
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
        }


        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.accountName,
            onValueChange = { onValueChange(accountDetails.copy(accountName = it)) },
            label = { Text("Account Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.initialBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialBalance = it)) },
            label = { Text("Initial Balance *") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.initialDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(initialDate = it)) },
            label = { Text("Opening Date  *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.notes.toString(),
            onValueChange = { onValueChange(accountDetails.copy(notes = it)) },
            label = { Text("Notes") },
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        Text(
            modifier = Modifier.padding(0.dp, 8.dp),
            text = "Others",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.accountNum.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accountNum = it)) },
            label = { Text("Account Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.heldAt.toString(),
            onValueChange = { onValueChange(accountDetails.copy(heldAt = it)) },
            label = { Text("Held At") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.website.toString(),
            onValueChange = { onValueChange(accountDetails.copy(website = it)) },
            label = { Text("Website") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.contactInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(contactInfo = it)) },
            label = { Text("Contact Information") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.accessInfo.toString(),
            onValueChange = { onValueChange(accountDetails.copy(accessInfo = it)) },
            label = { Text("Access Information") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        Text("Statement")

        Row(
            modifier = Modifier.padding(0.dp, 8.dp),
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
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.statementDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(statementDate = it)) },
            label = { Text("Statement Date") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.minimumBalance.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumBalance = it)) },
            label = { Text("Minimum Balance") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        Text("Credit")

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.creditLimit.toString(),
            onValueChange = { onValueChange(accountDetails.copy(creditLimit = it)) },
            label = { Text("Credit Limit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })

        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.interestRate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(interestRate = it)) },
            label = { Text("Interest Rate") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.paymentDueDate.toString(),
            onValueChange = { onValueChange(accountDetails.copy(paymentDueDate = it)) },
            label = { Text("Payment Due Date") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = accountDetails.minimumPayment.toString(),
            onValueChange = { onValueChange(accountDetails.copy(minimumPayment = it)) },
            label = { Text("Minimum Payment") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountEntryFormPreview() {
    ExpenseTrackerTheme {
        AccountEntryScreen()
    }
}