package com.seyone22.expensetracker.ui.screen.operations.transaction

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.data.model.RepeatFrequency
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.askNotificationPermissions
import com.seyone22.expensetracker.ui.common.scheduleWorkByDayCount
import com.seyone22.expensetracker.ui.common.scheduleWorkByMonthCount
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.composables.EditableTransactionForm
import com.seyone22.expensetracker.ui.screen.operations.transaction.composables.TransactionEntryForm
import kotlinx.coroutines.launch
import java.util.Locale

object TransactionEntryDestination : NavigationDestination {
    override val route = "TransactionEntry"
    override val titleRes = R.string.app_name
    override val routeId = 11
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    context: Context = LocalContext.current
) {
    val transactionUiState by viewModel.transactionUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var recurring by remember { mutableStateOf(false) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ), title = {
            Text(
                text = "Create Transaction", style = MaterialTheme.typography.titleLarge
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navigateBack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Close"
                )
            }
        }, actions = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (!recurring) {
                            viewModel.saveTransaction()
                        } else {
                            val recurrenceDetails =
                                viewModel.transactionUiState.value.billsDepositsDetails
                            // Get permissions
                            askNotificationPermissions(context)

                            val repeatsVal = RepeatFrequency.valueOf(
                                recurrenceDetails.REPEATS.uppercase(Locale.ROOT)
                            )

                            try {
                                if (repeatsVal.dayCount > 0) {
                                    scheduleWorkByMonthCount(context, recurrenceDetails)
                                } else if (repeatsVal.dayCount < 0) {
                                    scheduleWorkByDayCount(context, recurrenceDetails)
                                } else {
                                    throw Exception("Unsupported timeframe!")
                                }
                                viewModel.saveRecurringTransaction()
                                Toast.makeText(
                                    context,
                                    "Successfully scheduled transaction!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error!\n$e", Toast.LENGTH_SHORT).show()
                            }
                        }
                        navigateBack()
                    }
                }, enabled = if (!recurring) {
                    transactionUiState.isEntryValid
                } else {
                    transactionUiState.isRecurringEntryValid && transactionUiState.isEntryValid
                }, modifier = modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
            ) {
                Text(
                    text = if (recurring) {
                        "Create Rule"
                    } else {
                        "Create"
                    }
                )
            }
        })

    }

    ) { padding ->
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding),
                ) {
                    TransactionEntryForm(
                        transactionDetails = transactionUiState.transactionDetails,
                        transactionUiState = transactionUiState,
                        onValueChange = viewModel::updateUiState,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        edit = false
                    )
                    EditableTransactionForm(
                        editableTransactionDetails = transactionUiState.billsDepositsDetails,
                        onValueChange = viewModel::updateUiState,
                        viewModel = viewModel,
                        setRecurring = { recurring = !recurring },
                        coroutineScope = coroutineScope,
                        edit = false
                    )
                }
            }
        }
    }
}