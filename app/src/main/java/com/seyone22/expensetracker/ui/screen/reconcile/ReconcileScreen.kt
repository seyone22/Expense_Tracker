package com.seyone22.expensetracker.ui.screen.reconcile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.utils.formatCurrency
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController

object ReconcileDestination : NavigationDestination {
    override val route = "Reconcile"
    override val titleRes = com.seyone22.expensetracker.R.string.app_name
    override val routeId = 99
    override val icon = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconcileScreen(
    modifier: Modifier = Modifier,
    accountId: String,
    navController: NavController,
    viewModel: ReconcileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Initialize data on start
    LaunchedEffect(accountId) {
        viewModel.initReconciliation(accountId.toInt())
    }

    var endingBalanceInput by remember { mutableStateOf("") }
    var statementDateInput by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var openDatePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ExpenseTopBar(
                selectedActivity = "Account Reconciliation",
                hasNavigation = true,
                navController = navController
            )
        }
    ) { paddingValues ->
        if (!uiState.isWizardStarted) {
            // Step 1: Initial Setup Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Reconciliation Information",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Account Reconciliation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Verify your account transactions match your bank statement. Enter your statement details below to begin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = endingBalanceInput,
                    onValueChange = { endingBalanceInput = it },
                    label = { Text("Statement Ending Balance*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = statementDateInput,
                    onValueChange = { statementDateInput = it },
                    label = { Text("Statement Date*") },
                    singleLine = true,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { openDatePickerDialog = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val endBal = endingBalanceInput.toDoubleOrNull()
                        if (endBal != null) {
                            viewModel.startWizard(endBal, statementDateInput)
                        } else {
                            Toast.makeText(context, "Please enter a valid ending balance", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Start Reconciliation")
                }
            }

            // Date Picker Dialog
            if (openDatePickerDialog) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { openDatePickerDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                statementDateInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                            }
                            openDatePickerDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDatePickerDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

        } else {
            // Step 2: Transaction Checklist Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ending Balance:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = String.format("%.2f", uiState.endingBalance),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cleared Balance:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = String.format("%.2f", uiState.startingBalance + uiState.selectedTotal),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Difference:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isZero = Math.abs(uiState.difference) < 0.01
                                Icon(
                                    imageVector = if (isZero) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isZero) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.2f", uiState.difference),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isZero) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Scrollable List
                Text(
                    text = "Select transactions that appear on statement:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.unreconciledTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No unreconciled transactions found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.unreconciledTransactions) { transaction ->
                            val isChecked = uiState.checkedIds.contains(transaction.transId)
                            val isDeposit = transaction.transCode == "Deposit"
                            val isTransferToMe = transaction.transCode == "Transfer" && transaction.toAccountId == uiState.account.accountId

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleTransactionChecked(transaction.transId) }
                                    .background(
                                        if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleTransactionChecked(transaction.transId) }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = transaction.payeeName ?: (if (transaction.transCode == "Transfer") "Transfer" else "No Payee"),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = transaction.transDate ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = (if (isDeposit || isTransferToMe) "+" else "-") + String.format("%.2f", transaction.transAmount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDeposit || isTransferToMe) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                                )
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                // Bottom actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = viewModel.completeReconciliation()
                                if (success) {
                                    Toast.makeText(context, "Reconciliation Completed Successfully!", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to update transaction status", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = uiState.isCompleteEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}
