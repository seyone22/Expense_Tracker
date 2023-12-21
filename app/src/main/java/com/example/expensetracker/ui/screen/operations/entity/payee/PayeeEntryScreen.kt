package com.example.expensetracker.ui.screen.operations.entity.payee

import android.annotation.SuppressLint
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch

object PayeeEntryDestination : NavigationDestination {
    override val route = "EnterPayee"
    override val titleRes = R.string.app_name
    override val routeId = 17
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayeeEntryScreen(
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: PayeeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(
                        text = "Create Payee",
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
                            viewModel.savePayee()
                            navigateBack(

                            )
                        }
                        },
                        modifier = modifier.padding(0.dp,0.dp,8.dp,0.dp),
                        enabled = viewModel.payeeUiState.isEntryValid
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { padding ->
        PayeeEntryBody(
            payeeUiState = viewModel.payeeUiState,
            onPayeeValueChange = viewModel::updateUiState,
            modifier = modifier.padding(padding)
        )
    }

}

@Composable
fun PayeeEntryBody(
    payeeUiState: PayeeUiState = PayeeUiState(),
    onPayeeValueChange: (PayeeDetails) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            PayeeEntryForm(
                payeeDetails = payeeUiState.payeeDetails,
                onValueChange = onPayeeValueChange,
                modifier = Modifier
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayeeEntryForm(
    payeeDetails: PayeeDetails,
    onValueChange: (PayeeDetails) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var payeeTypeExpanded by remember { mutableStateOf(false) }
    var openInitialDateDialog by remember { mutableStateOf(false) }
    var openPaymentDueDateDialog by remember { mutableStateOf(false) }
    var openStatementDateDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .focusGroup()
            .padding(0.dp, 8.dp)
    )
    {
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = payeeDetails.payeeName,
            onValueChange = { onValueChange(payeeDetails.copy(payeeName = it)) },
            label = { Text("Payee Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        Row(
            modifier = Modifier.padding(0.dp, 8.dp),
        ) {
            Checkbox(
                checked = payeeDetails.active.toBoolean(),
                onCheckedChange = { onValueChange(payeeDetails.copy(active = (it).toString())) },
            )
            Text(
                text = "Hidden",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(CenterVertically)
            )
        }
// We're obviously not including last used category -_-
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = payeeDetails.number,
            onValueChange = { onValueChange(payeeDetails.copy(number = it)) },
            label = { Text("Reference Number") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = payeeDetails.website,
            onValueChange = { onValueChange(payeeDetails.copy(website = it)) },
            label = { Text("Website") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = payeeDetails.notes,
            onValueChange = { onValueChange(payeeDetails.copy(notes = it)) },
            label = { Text("Notes") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PayeeEntryFormPreview() {
    ExpenseTrackerTheme {
        PayeeEntryScreen()
    }
}