package com.seyone22.expensetracker.ui.screen.operations.transaction.composables

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Attachment
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.RepeatFrequency
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.dialogs.AddEditCategoryDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.AddEditPayeeDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.SplitDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryForm(
    modifier: Modifier = Modifier,
    viewModel: TransactionEntryViewModel,
    entityViewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope,
    edit: Boolean,
    onRecurringChanged: (Boolean) -> Unit = {}
) {
    val transactionUiState by viewModel.transactionUiState.collectAsState()
    val entityList by viewModel.entityList.collectAsState()
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    // Current dialog state
    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    // Modal sheet controls
    var showAccountSheet by remember { mutableStateOf(false) }
    var showToAccountSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showPayeeSheet by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }

    // Dropdown query filters
    var accountSearch by remember { mutableStateOf("") }
    var toAccountSearch by remember { mutableStateOf("") }
    var categorySearch by remember { mutableStateOf("") }
    var payeeSearch by remember { mutableStateOf("") }

    // Date controls
    var openTransactionDateDialog by remember { mutableStateOf(false) }
    var openDateDueDialog by remember { mutableStateOf(false) }

    var showMore by remember { mutableStateOf(false) }
    var showRecurringFields by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Local selections
    var currentAccount by remember { mutableStateOf(Account()) }
    var currentPayee by remember { mutableStateOf(Payee()) }
    var currentCategory by remember { mutableStateOf(Category()) }
    var currentToAccount by remember { mutableStateOf(Account()) }
    
    val isTransfer = transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName
    var currentAdvancedAmount by remember { 
        mutableDoubleStateOf(
            if (isTransfer) (transactionUiState.transactionDetails.toTransAmount.toDoubleOrNull() ?: 0.0) 
            else 0.0
        ) 
    }

    // Active input for numeric keyboard: "Amount", "DepositAmount", or "ExchangeRate"
    var activeInputField by remember { mutableStateOf("Amount") }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val path = copyUriToInternalStorage(context, it)
                path?.let { p -> viewModel.addAttachment(p) }
            }
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val path = copyUriToInternalStorage(context, it)
                path?.let { p -> viewModel.addAttachment(p) }
            }
        }
    }

    var currentCurrency: CurrencyFormat? by remember { mutableStateOf(null) }
    var currentCurrencyAdvanced: CurrencyFormat? by remember { mutableStateOf(null) }
    val currenciesDiffer = isTransfer && currentCurrency != null && currentCurrencyAdvanced != null && (currentCurrency?.currencyId != currentCurrencyAdvanced?.currencyId)

    val amountVal = transactionUiState.transactionDetails.transAmount.toDoubleOrNull() ?: 0.0
    val rateVal = if (amountVal > 0.0) currentAdvancedAmount / amountVal else 0.0
    var rateText by remember(amountVal, currentAdvancedAmount) {
        mutableStateOf(if (amountVal > 0.0) String.format(Locale.US, "%.6f", rateVal) else "")
    }

    LaunchedEffect(currentAccount, currentToAccount) {
        coroutineScope.launch {
            if (transactionUiState.transactionDetails.accountId.isNotBlank()) {
                currentAccount = viewModel.getAccount(transactionUiState.transactionDetails.accountId.toInt())
                currentCurrency = sharedViewModel.getCurrencyById(currentAccount.currencyId)
            }
            if (transactionUiState.transactionDetails.toAccountId.isNotBlank() && transactionUiState.transactionDetails.toAccountId != "-1") {
                currentToAccount = viewModel.getAccount(transactionUiState.transactionDetails.toAccountId.toInt())
                currentCurrencyAdvanced = sharedViewModel.getCurrencyById(currentToAccount.currencyId)
            }
        }
    }

    if (edit) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                if (transactionUiState.transactionDetails.accountId.isNotBlank()) {
                    currentAccount = viewModel.getAccount(transactionUiState.transactionDetails.accountId.toInt())
                }
                if (transactionUiState.transactionDetails.payeeId.isNotBlank() && transactionUiState.transactionDetails.payeeId != "-1") {
                    currentPayee = viewModel.getPayee(transactionUiState.transactionDetails.payeeId.toInt())
                }
                if (transactionUiState.transactionDetails.categoryId.isNotBlank() && transactionUiState.transactionDetails.categoryId != "-1") {
                    currentCategory = viewModel.getCategory(transactionUiState.transactionDetails.categoryId.toInt())
                }
                if (transactionUiState.transactionDetails.toAccountId.isNotBlank() && transactionUiState.transactionDetails.toAccountId != "-1") {
                    currentToAccount = viewModel.getAccount(transactionUiState.transactionDetails.toAccountId.toInt())
                }
            }
        }
    }

    fun updateActiveValue(value: String) {
        val details = transactionUiState.transactionDetails
        when (activeInputField) {
            "Amount" -> {
                val cleanAmount = com.seyone22.expensetracker.utils.FinancialStatsCalculator.evaluateExpression(value)
                val parsed = cleanAmount.toDoubleOrNull() ?: 0.0
                if (isTransfer) {
                    if (currenciesDiffer) {
                        val currentRate = rateText.toDoubleOrNull() ?: 1.0
                        currentAdvancedAmount = parsed * currentRate
                    } else {
                        currentAdvancedAmount = parsed
                    }
                }
                viewModel.updateUiState(
                    details.copy(transAmount = value),
                    transactionUiState.billsDepositsDetails,
                    currentAdvancedAmount
                )
            }
            "DepositAmount" -> {
                val parsed = value.toDoubleOrNull() ?: 0.0
                currentAdvancedAmount = parsed
                if (amountVal > 0.0) {
                    rateText = String.format(Locale.US, "%.6f", parsed / amountVal)
                }
                viewModel.updateUiState(
                    details,
                    transactionUiState.billsDepositsDetails,
                    parsed
                )
            }
            "ExchangeRate" -> {
                rateText = value
                val parsedRate = value.toDoubleOrNull() ?: 0.0
                if (parsedRate > 0.0) {
                    currentAdvancedAmount = amountVal * parsedRate
                    viewModel.updateUiState(
                        details,
                        transactionUiState.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                }
            }
        }
    }

    // Keypad input handlers
    val onDigitPress: (String) -> Unit = { digit ->
        val currentStr = when (activeInputField) {
            "Amount" -> transactionUiState.transactionDetails.transAmount
            "DepositAmount" -> if (currentAdvancedAmount == 0.0) "" else currentAdvancedAmount.toString()
            else -> rateText
        }
        val cleanStr = if (currentStr == "0" || currentStr == "0.0") "" else currentStr
        val newStr = cleanStr + digit
        updateActiveValue(newStr)
    }

    val onOperatorPress: (String) -> Unit = { op ->
        if (activeInputField == "Amount") {
            val currentStr = transactionUiState.transactionDetails.transAmount
            if (currentStr.isNotEmpty() && !currentStr.endsWith(" ") && !currentStr.endsWith("+") && !currentStr.endsWith("-")) {
                updateActiveValue("$currentStr $op ")
            }
        }
    }

    val onBackspace: () -> Unit = {
        val currentStr = when (activeInputField) {
            "Amount" -> transactionUiState.transactionDetails.transAmount
            "DepositAmount" -> if (currentAdvancedAmount == 0.0) "" else currentAdvancedAmount.toString()
            else -> rateText
        }
        if (currentStr.isNotEmpty()) {
            val trimmed = currentStr.trimEnd()
            val newStr = if (trimmed.endsWith("+") || trimmed.endsWith("-")) {
                trimmed.dropLast(1).trimEnd()
            } else {
                currentStr.dropLast(1)
            }
            updateActiveValue(if (newStr.isEmpty()) "0" else newStr)
        }
    }

    val onClear: () -> Unit = {
        updateActiveValue("0")
    }

    val onEvaluate: () -> Unit = {
        if (activeInputField == "Amount") {
            val currentStr = transactionUiState.transactionDetails.transAmount
            val result = com.seyone22.expensetracker.utils.FinancialStatsCalculator.evaluateExpression(currentStr)
            updateActiveValue(result)
        }
    }

    // MAIN SCREEN LAYOUT
    Column(
        modifier = modifier.fillMaxHeight()
    ) {
        // SCROLLABLE CONTENT AREA
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Transaction Type selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(TransactionCode.WITHDRAWAL, TransactionCode.DEPOSIT, TransactionCode.TRANSFER).forEach { code ->
                    val selected = transactionUiState.transactionDetails.transCode == code.displayName
                    val displayLabel = when (code) {
                        TransactionCode.WITHDRAWAL -> "Expense"
                        TransactionCode.DEPOSIT -> "Income"
                        else -> "Transfer"
                    }
                    val color = if (selected) {
                        when (code) {
                            TransactionCode.WITHDRAWAL -> MaterialTheme.colorScheme.errorContainer
                            TransactionCode.DEPOSIT -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    } else Color.Transparent

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color)
                            .clickable {
                                viewModel.updateUiState(
                                    transactionUiState.transactionDetails.copy(transCode = code.displayName),
                                    transactionUiState.billsDepositsDetails,
                                    currentAdvancedAmount
                                )
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayLabel,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) {
                                when (code) {
                                    TransactionCode.WITHDRAWAL -> MaterialTheme.colorScheme.onErrorContainer
                                    TransactionCode.DEPOSIT -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            } else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Large Amount Display
            val amountColor = when (transactionUiState.transactionDetails.transCode) {
                TransactionCode.WITHDRAWAL.displayName -> MaterialTheme.colorScheme.error
                TransactionCode.DEPOSIT.displayName -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.secondary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (activeInputField == "Amount") 2.dp else 0.dp,
                        color = if (activeInputField == "Amount") amountColor else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { activeInputField = "Amount" },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentCurrency?.currency_symbol ?: "$",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = amountColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = transactionUiState.transactionDetails.transAmount.ifEmpty { "0" },
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 44.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = amountColor,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = if (isTransfer) "Source Amount" else "Transaction Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // 3. Sub-inputs for multi-currency transfers
            if (isTransfer && currenciesDiffer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (activeInputField == "DepositAmount") 2.dp else 0.dp,
                                color = if (activeInputField == "DepositAmount") MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { activeInputField = "DepositAmount" },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Deposit (${currentCurrencyAdvanced?.currency_symbol ?: ""})", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (currentAdvancedAmount == 0.0) "0" else currentAdvancedAmount.toString(), 
                                style = MaterialTheme.typography.bodyLarge, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (activeInputField == "ExchangeRate") 2.dp else 0.dp,
                                color = if (activeInputField == "ExchangeRate") MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { activeInputField = "ExchangeRate" },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Exchange Rate", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rateText.ifEmpty { "1.0" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Selector Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Account & Category (or To Account)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectorCard(
                        label = if (isTransfer) "From Account" else "Account",
                        value = currentAccount.accountName.ifEmpty { "Choose account" },
                        icon = Icons.Outlined.AccountBalance,
                        onClick = { showAccountSheet = true },
                        modifier = Modifier.weight(1f)
                    )
                    if (isTransfer) {
                        SelectorCard(
                            label = "To Account",
                            value = currentToAccount.accountName.ifEmpty { "Choose account" },
                            icon = Icons.Outlined.CompareArrows,
                            onClick = { showToAccountSheet = true },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        SelectorCard(
                            label = "Category",
                            value = removeTrPrefix(currentCategory.categName).ifEmpty { "Choose category" },
                            icon = Icons.Outlined.Category,
                            onClick = { showCategorySheet = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2: Payee (if not Transfer) & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isTransfer) {
                        SelectorCard(
                            label = if (transactionUiState.transactionDetails.transCode == TransactionCode.DEPOSIT.displayName) "From" else "Payee",
                            value = currentPayee.payeeName.ifEmpty { "Choose payee" },
                            icon = Icons.Outlined.Person,
                            onClick = { showPayeeSheet = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SelectorCard(
                        label = "Date",
                        value = transactionUiState.transactionDetails.transDate,
                        icon = Icons.Outlined.CalendarToday,
                        onClick = { openTransactionDateDialog = true },
                        modifier = Modifier.weight(if (isTransfer) 2f else 1f)
                    )
                }

                // Notes Field Card
                SelectorCard(
                    label = "Notes",
                    value = transactionUiState.transactionDetails.notes.ifEmpty { "Add optional details..." },
                    icon = Icons.Outlined.Notes,
                    onClick = { showNotesDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 5. Status filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    TransactionStatus.entries.forEach { status ->
                        item {
                            FilterChip(
                                selected = transactionUiState.transactionDetails.status == status.displayName,
                                onClick = {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(status = status.displayName),
                                        transactionUiState.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                },
                                label = { Text(status.displayName) }
                            )
                        }
                    }
                }
            }

            // 6. Splits Section (Only for non-transfers)
            if (!isTransfer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Split Transaction", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = transactionUiState.isSplit,
                        onCheckedChange = { viewModel.toggleSplit(it) }
                    )
                }

                if (transactionUiState.isSplit) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Splitted Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            transactionUiState.splits.forEachIndexed { index, split ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Split category select
                                    var splitCategoryExpanded by remember { mutableStateOf(false) }
                                    Box(modifier = Modifier.weight(1.5f)) {
                                        TextButton(onClick = { splitCategoryExpanded = true }) {
                                            val categ = entityList.categoriesList.find { it.categId.toString() == split.categId }
                                            Text(
                                                text = removeTrPrefix(categ?.categName ?: "Category"),
                                                maxLines = 1
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = splitCategoryExpanded,
                                            onDismissRequest = { splitCategoryExpanded = false }
                                        ) {
                                            entityList.categoriesList.forEach { category ->
                                                DropdownMenuItem(
                                                    text = { Text(removeTrPrefix(category.categName)) },
                                                    onClick = {
                                                        viewModel.updateSplit(index, split.copy(categId = category.categId.toString()))
                                                        splitCategoryExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Split amount
                                    OutlinedTextField(
                                        value = split.splitAmount,
                                        onValueChange = { viewModel.updateSplit(index, split.copy(splitAmount = it)) },
                                        label = { Text("Amount") },
                                        modifier = Modifier.weight(1.2f),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                    )

                                    // Remove split
                                    IconButton(onClick = { viewModel.removeSplitRow(index) }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Remove Split", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { viewModel.addSplitRow() }) {
                                    Text("+ Add Item")
                                }
                                val splitSum = transactionUiState.splits.sumOf { it.splitAmount.toDoubleOrNull() ?: 0.0 }
                                Text(
                                    text = "Sum: ${String.format("%.2f", splitSum)} / ${String.format("%.2f", amountVal)}",
                                    color = if (Math.abs(amountVal - splitSum) < 0.01) Color.Unspecified else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // 7. Recurring Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recurring Transaction", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = showRecurringFields,
                    onCheckedChange = {
                        showRecurringFields = it
                        onRecurringChanged(it)
                    }
                )
            }

            if (showRecurringFields) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Due date card
                        SelectorCard(
                            label = "Date Due *",
                            value = transactionUiState.billsDepositsDetails.NEXTOCCURRENCEDATE,
                            icon = Icons.Outlined.CalendarToday,
                            onClick = { openDateDueDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Frequency Dropdown
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = !statusExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { statusExpanded = true }
                                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                                value = transactionUiState.billsDepositsDetails.REPEATS,
                                readOnly = true,
                                onValueChange = {},
                                label = { Text("Repeat Frequency *") },
                                singleLine = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                enumValues<RepeatFrequency>().forEach { frequency ->
                                    DropdownMenuItem(
                                        text = { Text(frequency.displayName) },
                                        onClick = {
                                            viewModel.updateUiState(
                                                transactionUiState.transactionDetails,
                                                transactionUiState.billsDepositsDetails.copy(REPEATS = frequency.displayName),
                                                currentAdvancedAmount
                                            )
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Payments left
                        OutlinedTextField(
                            value = transactionUiState.billsDepositsDetails.NUMOCCURRENCES,
                            onValueChange = {
                                viewModel.updateUiState(
                                    transactionUiState.transactionDetails,
                                    transactionUiState.billsDepositsDetails.copy(NUMOCCURRENCES = it),
                                    currentAdvancedAmount
                                )
                            },
                            label = { Text("Payments Left") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }

            // 8. Show More toggle
            TextButton(onClick = { showMore = !showMore }) {
                Text(if (showMore) "Show less" else "Show more options")
            }

            if (showMore) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = transactionUiState.transactionDetails.transactionNumber,
                        onValueChange = {
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(transactionNumber = it),
                                transactionUiState.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        },
                        label = { Text("Reference Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = transactionUiState.transactionDetails.color,
                        onValueChange = {
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(color = it),
                                transactionUiState.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        },
                        label = { Text("Color Tag (Number)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Receipt Attachments Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Receipt Attachments (${transactionUiState.attachments.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Add Image")
                                }
                                Button(
                                    onClick = { fileLauncher.launch("*/*") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Add File")
                                }
                            }
                            transactionUiState.attachments.forEach { path ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(File(path).name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.removeAttachment(path) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove attachment", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FIXED KEYPAD AT THE BOTTOM
        CalculatorKeypad(
            onDigitPress = onDigitPress,
            onOperatorPress = onOperatorPress,
            onBackspace = onBackspace,
            onClear = onClear,
            onEvaluate = onEvaluate,
            modifier = Modifier.fillMaxWidth()
        )
    }

    // MODAL BOTTOM SHEETS
    // 1. Account Picker Sheet
    if (showAccountSheet) {
        ModalBottomSheet(onDismissRequest = { showAccountSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.6f)) {
                Text("Select Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = accountSearch,
                    onValueChange = { accountSearch = it },
                    label = { Text("Search Account") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    trailingIcon = { if (accountSearch.isNotEmpty()) IconButton(onClick = { accountSearch = "" }) { Icon(Icons.Default.Close, "") } }
                )
                val filteredAccounts = entityList.accountsList.filter { it.accountName.contains(accountSearch, ignoreCase = true) }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredAccounts) { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(accountId = account.accountId.toString()),
                                        transactionUiState.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    currentAccount = account
                                    showAccountSheet = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(account.accountName, style = MaterialTheme.typography.bodyLarge)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }

    // 2. To Account Picker Sheet (for Transfers)
    if (showToAccountSheet) {
        ModalBottomSheet(onDismissRequest = { showToAccountSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.6f)) {
                Text("Select To Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = toAccountSearch,
                    onValueChange = { toAccountSearch = it },
                    label = { Text("Search Destination Account") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    trailingIcon = { if (toAccountSearch.isNotEmpty()) IconButton(onClick = { toAccountSearch = "" }) { Icon(Icons.Default.Close, "") } }
                )
                val filteredAccounts = entityList.accountsList.filter { it.accountName.contains(toAccountSearch, ignoreCase = true) }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredAccounts) { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(toAccountId = account.accountId.toString()),
                                        transactionUiState.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    currentToAccount = account
                                    showToAccountSheet = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(account.accountName, style = MaterialTheme.typography.bodyLarge)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }

    // 3. Category Picker Sheet
    if (showCategorySheet) {
        ModalBottomSheet(onDismissRequest = { showCategorySheet = false }) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.7f)) {
                Text("Select Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = categorySearch,
                    onValueChange = { categorySearch = it },
                    label = { Text("Search Category") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    trailingIcon = { if (categorySearch.isNotEmpty()) IconButton(onClick = { categorySearch = "" }) { Icon(Icons.Default.Close, "") } }
                )

                // Add Category Button
                Button(
                    onClick = {
                        viewModel.showDialog(
                            AddEditCategoryDialogAction(
                                onAdd = { category ->
                                    coroutineScope.launch {
                                        entityViewModel.saveCategory(category)
                                        viewModel.updateCategoriesList()
                                        currentCategory = category
                                        viewModel.updateUiState(
                                            transactionUiState.transactionDetails.copy(categoryId = category.categId.toString()),
                                            transactionUiState.billsDepositsDetails,
                                            currentAdvancedAmount
                                        )
                                        showCategorySheet = false
                                    }
                                },
                                onEdit = { category ->
                                    coroutineScope.launch {
                                        entityViewModel.editCategory(category)
                                        viewModel.updateCategoriesList()
                                    }
                                }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Category")
                }

                val filtered = entityList.categoriesList.filter { it.categName.contains(categorySearch, ignoreCase = true) }
                val parents = filtered.filter { it.parentId == -1 }
                val childrenMap = filtered.filter { it.parentId != -1 }.groupBy { it.parentId }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    parents.forEach { parent ->
                        item {
                            Text(
                                text = removeTrPrefix(parent.categName),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateUiState(
                                            transactionUiState.transactionDetails.copy(categoryId = parent.categId.toString()),
                                            transactionUiState.billsDepositsDetails,
                                            currentAdvancedAmount
                                        )
                                        currentCategory = parent
                                        showCategorySheet = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp)
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f))
                        }

                        val children = childrenMap[parent.categId] ?: emptyList()
                        items(children) { child ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateUiState(
                                            transactionUiState.transactionDetails.copy(categoryId = child.categId.toString()),
                                            transactionUiState.billsDepositsDetails,
                                            currentAdvancedAmount
                                        )
                                        currentCategory = child
                                        showCategorySheet = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 20.dp)
                            ) {
                                Text(removeTrPrefix(child.categName), style = MaterialTheme.typography.bodyLarge)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }

    // 4. Payee Picker Sheet
    if (showPayeeSheet) {
        ModalBottomSheet(onDismissRequest = { showPayeeSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.7f)) {
                Text("Select Payee", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = payeeSearch,
                    onValueChange = { payeeSearch = it },
                    label = { Text("Search Payee") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    trailingIcon = { if (payeeSearch.isNotEmpty()) IconButton(onClick = { payeeSearch = "" }) { Icon(Icons.Default.Close, "") } }
                )

                // Add Payee Button
                Button(
                    onClick = {
                        viewModel.showDialog(
                            AddEditPayeeDialogAction(
                                onAdd = { payee ->
                                    coroutineScope.launch {
                                        entityViewModel.savePayee(payee)
                                        viewModel.updatePayeesList()
                                        currentPayee = payee
                                        viewModel.updateUiState(
                                            transactionUiState.transactionDetails.copy(
                                                toAccountId = "-1",
                                                payeeId = payee.payeeId.toString()
                                            ),
                                            transactionUiState.billsDepositsDetails,
                                            currentAdvancedAmount
                                        )
                                        showPayeeSheet = false
                                    }
                                },
                                onEdit = { payee ->
                                    coroutineScope.launch {
                                        entityViewModel.editPayee(payee)
                                        viewModel.updatePayeesList()
                                    }
                                }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Payee")
                }

                val filteredPayees = entityList.payeesList.filter { it.payeeName.contains(payeeSearch, ignoreCase = true) }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredPayees) { payee ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            toAccountId = "-1",
                                            payeeId = payee.payeeId.toString()
                                        ),
                                        transactionUiState.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    currentPayee = payee
                                    showPayeeSheet = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(payee.payeeName, style = MaterialTheme.typography.bodyLarge)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }

    // NOTES DIALOG
    if (showNotesDialog) {
        var notesInput by remember { mutableStateOf(transactionUiState.transactionDetails.notes) }
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Transaction Notes") },
            text = {
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Add details, notes, tag names...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(notes = notesInput),
                            transactionUiState.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        showNotesDialog = false
                    }
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DATE PICKERS
    if (openTransactionDateDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = { openTransactionDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openTransactionDateDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(transDate = dateFormat.format(date)),
                            transactionUiState.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    }, 
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, 
            dismissButton = {
                TextButton(onClick = { openTransactionDateDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (openDateDueDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = { openDateDueDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDateDueDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails,
                            transactionUiState.billsDepositsDetails.copy(NEXTOCCURRENCEDATE = dateFormat.format(date)),
                            currentAdvancedAmount
                        )
                    }, 
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, 
            dismissButton = {
                TextButton(onClick = { openDateDueDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Key-value Tappable Card
@Composable
fun SelectorCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}

// Built-in Calculator numpad Keypad
@Composable
fun CalculatorKeypad(
    onDigitPress: (String) -> Unit,
    onOperatorPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        listOf("7", "8", "9", "⌫"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("C", "0", ".", "=")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { char ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when (char) {
                                    "⌫", "C" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                                    "+", "-", "=" -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable {
                                when (char) {
                                    "⌫" -> onBackspace()
                                    "C" -> onClear()
                                    "=" -> onEvaluate()
                                    "+", "-" -> onOperatorPress(char)
                                    else -> onDigitPress(char)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = when (char) {
                                "⌫", "C" -> MaterialTheme.colorScheme.onErrorContainer
                                "+", "-", "=" -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}


fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val contentResolver = context.contentResolver
        val mimeTypeMap = android.webkit.MimeTypeMap.getSingleton()
        val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
        
        val attachmentsDir = File(context.filesDir, "attachments")
        if (!attachmentsDir.exists()) {
            attachmentsDir.mkdirs()
        }
        
        val targetFile = File(attachmentsDir, "receipt_${System.currentTimeMillis()}.$extension")
        contentResolver.openInputStream(uri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}