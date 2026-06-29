package com.seyone22.expensetracker.ui.screen.operations.transaction.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File
import androidx.compose.material3.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.mutableStateMapOf
import com.seyone22.expensetracker.ui.screen.operations.transaction.SplitDetails
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.dialogs.AddEditCategoryDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.AddEditPayeeDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    focusManager: FocusManager = LocalFocusManager.current
) {
    val transactionUiState by viewModel.transactionUiState.collectAsState()
    val entityList by viewModel.entityList.collectAsState()

    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    var showMore by remember { mutableStateOf(false) }

    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var tagsExpanded by remember { mutableStateOf(false) }

    /*    var tagFilter by remember { mutableStateOf("") }
        val filteredTags = data.tags.filter {
            (it?.name ?: "").contains(
                tagFilter, true
            )
        }*/

    var openTransactionDateDialog by remember { mutableStateOf(false) }

    var currentAccount by remember { mutableStateOf(Account()) }
    var currentPayee by remember { mutableStateOf(Payee()) }
    var currentCategory by remember { mutableStateOf(Category()) }
    var currentToAccount by remember { mutableStateOf(Account()) }
    var currentAdvancedAmount by remember { mutableDoubleStateOf(if (transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName) (transactionUiState.transactionDetails.toTransAmount.toDoubleOrNull() ?: 0.0) else 0.0) }

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

    LaunchedEffect(currentAccount, currentToAccount) {
        coroutineScope.launch {
            if (transactionUiState.transactionDetails.accountId.isNotBlank()) {
                currentCurrency = sharedViewModel.getCurrencyById(currentAccount.currencyId)
            }
            if (transactionUiState.transactionDetails.toAccountId.isNotBlank()) {
                currentCurrencyAdvanced =
                    sharedViewModel.getCurrencyById(currentToAccount.currencyId)
            }
        }
    }

    if (edit) {
        coroutineScope.launch {
            currentAccount =
                viewModel.getAccount(transactionUiState.transactionDetails.accountId.toInt())
            currentPayee = viewModel.getPayee(transactionUiState.transactionDetails.payeeId.toInt())
            currentCategory =
                viewModel.getCategory(transactionUiState.transactionDetails.categoryId.toInt())
            currentToAccount =
                viewModel.getAccount(transactionUiState.transactionDetails.toAccountId.toInt())
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Transaction Type (transCode) Dropdown
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            enumValues<TransactionCode>().forEachIndexed { index, transactionCode ->
                SegmentedButton(
                    selected = transactionUiState.transactionDetails.transCode == transactionCode.displayName,
                    onClick = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(transCode = transactionCode.displayName),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    },
                    label = {
                        if (transactionCode.displayName == TransactionCode.WITHDRAWAL.displayName) Text(
                            "Withdraw"
                        ) else Text(transactionCode.displayName)
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = enumValues<TransactionCode>().size
                    )
                )
            }
        }

        // Transaction Date
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = true) {
                    openTransactionDateDialog = true
                },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            value = transactionUiState.transactionDetails.transDate,
            onValueChange = {
                viewModel.updateUiState(
                    transactionUiState.transactionDetails.copy(transDate = it),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { Text("Date of Transaction *") },
            readOnly = true,
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        // Transaction Status Dropdown
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionStatus.entries.forEach { transactionStatus ->
                item {
                    FilterChip(
                        selected = transactionUiState.transactionDetails.status == transactionStatus.displayName,
                        leadingIcon = {
                            if (transactionUiState.transactionDetails.status == transactionStatus.displayName) {
                                Icon(Icons.Filled.Check, "")
                            }
                        },
                        onClick = {
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(status = transactionStatus.displayName),
                                viewModel.transactionUiState.value.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        },
                        label = { Text(transactionStatus.displayName) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Transaction From Dropdown
        ExposedDropdownMenuBox(expanded = accountExpanded, onExpandedChange = {
            accountExpanded = !accountExpanded
        }) {
            OutlinedTextField(
                modifier = Modifier
                    .clickable(enabled = true) { accountExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                value = currentAccount.accountName,
                readOnly = true,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(accountId = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = {
                    when (transactionUiState.transactionDetails.transCode) {
                        TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                            Text(text = "Account *")
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            Text(text = "From Account *")
                        }
                    }
                },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                }),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
            )

            ExposedDropdownMenu(
                expanded = accountExpanded,
                onDismissRequest = { accountExpanded = false },
            ) {
                entityList.accountsList.forEach { account ->
                    DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(accountId = account.accountId.toString()),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                        currentAccount = account
                        accountExpanded = false
                    })
                }
            }
        }

        // Transaction Payee
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Payee/To Dropdown
            ExposedDropdownMenuBox(expanded = payeeExpanded, onExpandedChange = {
                payeeExpanded = !payeeExpanded
                when (transactionUiState.transactionDetails.transCode) {
                    TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                    }

                    TransactionCode.TRANSFER.displayName -> {
                    }
                }
            }) {
                OutlinedTextField(
                    modifier = Modifier
                        .clickable(enabled = true) { payeeExpanded = true }
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = when (transactionUiState.transactionDetails.transCode) {
                        TransactionCode.WITHDRAWAL.displayName, TransactionCode.DEPOSIT.displayName -> {
                            currentPayee.payeeName
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            currentToAccount.accountName
                        }

                        else -> {
                            ""
                        }
                    },
                    readOnly = true,
                    onValueChange = { },
                    label = {
                        when (transactionUiState.transactionDetails.transCode) {
                            TransactionCode.WITHDRAWAL.displayName -> {
                                Text(text = "Payee *")
                            }

                            TransactionCode.DEPOSIT.displayName -> {
                                Text(text = "From *")
                            }

                            TransactionCode.TRANSFER.displayName -> {
                                Text(text = "To Account *")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payeeExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = payeeExpanded,
                    onDismissRequest = { payeeExpanded = false },
                ) {
                    when (transactionUiState.transactionDetails.transCode) {
                        TransactionCode.DEPOSIT.displayName, TransactionCode.WITHDRAWAL.displayName -> {
                            entityList.payeesList.forEach { payee ->
                                DropdownMenuItem(text = { Text(payee.payeeName) }, onClick = {
                                    currentPayee = payee
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            toAccountId = "-1",
                                            payeeId = currentPayee.payeeId.toString()
                                        ),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    payeeExpanded = false
                                })
                            }
                        }

                        TransactionCode.TRANSFER.displayName -> {
                            entityList.accountsList.forEach { account ->
                                DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(
                                            payeeId = "-1",
                                            toAccountId = account.accountId.toString()
                                        ),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    currentToAccount = account
                                    payeeExpanded = false
                                })
                            }
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .padding(10.dp, 10.dp, 0.dp, 0.dp),
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
                                            payeeId = currentPayee.payeeId.toString()
                                        ),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                }
                            },
                            onEdit = { payee ->
                                coroutineScope.launch {
                                    entityViewModel.editPayee(payee)
                                    viewModel.updatePayeesList()
                                }
                            },
                        )
                    )
                },
                enabled = (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
            }
        }

        val isTransfer = transactionUiState.transactionDetails.transCode == TransactionCode.TRANSFER.displayName
        val currenciesDiffer = isTransfer && currentCurrency != null && currentCurrencyAdvanced != null && (currentCurrency?.currencyId != currentCurrencyAdvanced?.currencyId)

        val amountVal = transactionUiState.transactionDetails.transAmount.toDoubleOrNull() ?: 0.0
        val rateVal = if (amountVal > 0.0) currentAdvancedAmount / amountVal else 0.0
        var rateText by remember(amountVal, currentAdvancedAmount) {
            mutableStateOf(if (amountVal > 0.0) String.format(Locale.US, "%.6f", rateVal) else "")
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = transactionUiState.transactionDetails.transAmount,
            onValueChange = { input ->
                val parsedAmount = input.toDoubleOrNull() ?: 0.0
                if (isTransfer && currentCurrency != null && currentCurrencyAdvanced != null) {
                    if (currenciesDiffer) {
                        val currentRate = rateText.toDoubleOrNull() ?: (if (currentCurrencyAdvanced?.baseConvRate != 0.0) currentCurrency?.baseConvRate!! / currentCurrencyAdvanced?.baseConvRate!! else 1.0)
                        currentAdvancedAmount = parsedAmount * currentRate
                    } else {
                        currentAdvancedAmount = parsedAmount
                    }
                }
                viewModel.updateUiState(
                    transactionUiState.transactionDetails.copy(transAmount = input),
                    viewModel.transactionUiState.value.billsDepositsDetails,
                    currentAdvancedAmount
                )
            },
            label = { 
                Text(
                    if (isTransfer && currentCurrency != null) "Amount (${currentCurrency?.currencyName ?: ""})*"
                    else "Amount*"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (isTransfer) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = if (currentAdvancedAmount == 0.0) "" else currentAdvancedAmount.toString(),
                onValueChange = { input ->
                    val parsedDeposit = input.toDoubleOrNull() ?: 0.0
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails,
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        parsedDeposit
                    )
                    currentAdvancedAmount = parsedDeposit
                    if (amountVal > 0.0) {
                        rateText = String.format(Locale.US, "%.6f", parsedDeposit / amountVal)
                    }
                },
                label = { 
                    Text(
                        if (currentCurrencyAdvanced != null) "Deposit amount (${currentCurrencyAdvanced?.currencyName ?: ""})*"
                        else "Deposit amount*"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (currenciesDiffer) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = rateText,
                    onValueChange = { input ->
                        rateText = input
                        val parsedRate = input.toDoubleOrNull() ?: 0.0
                        if (parsedRate > 0.0) {
                            currentAdvancedAmount = amountVal * parsedRate
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails,
                                viewModel.transactionUiState.value.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        }
                    },
                    label = { 
                        Text("Exchange Rate (1 ${currentCurrency?.currency_symbol ?: ""} = X ${currentCurrencyAdvanced?.currency_symbol ?: ""})*")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }

        // Split Transaction Toggle (Only for non-transfers)
        if (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Split Transaction", modifier = Modifier.weight(1f))
                Switch(
                    checked = transactionUiState.isSplit,
                    onCheckedChange = { viewModel.toggleSplit(it) }
                )
            }
        }

        if (transactionUiState.isSplit && transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Split Items",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                transactionUiState.splits.forEachIndexed { index, split ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Category dropdown for this split
                                val splitCategory = entityList.categoriesList.firstOrNull { it.categId.toString() == split.categId } ?: Category(categName = "Select Category")
                                var expanded by remember { mutableStateOf(false) }

                                Box(modifier = Modifier.weight(1f)) {
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded }
                                    ) {
                                        OutlinedTextField(
                                            modifier = Modifier
                                                .clickable { expanded = true }
                                                .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                                            value = removeTrPrefix(splitCategory.categName),
                                            readOnly = true,
                                            onValueChange = {},
                                            label = { Text("Category *") },
                                            singleLine = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            val parentCategories = entityList.categoriesList.filter { it.parentId == -1 }
                                            val childCategoriesMap = entityList.categoriesList.filter { it.parentId != -1 }
                                                .groupBy { it.parentId }

                                            for (parent in parentCategories) {
                                                DropdownMenuItem(
                                                    text = { Text(removeTrPrefix(parent.categName)) },
                                                    onClick = {
                                                        viewModel.updateSplit(index, split.copy(categId = parent.categId.toString()))
                                                        expanded = false
                                                    }
                                                )
                                                childCategoriesMap[parent.categId]?.forEach { child ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Row {
                                                                Spacer(modifier = Modifier.width(16.dp))
                                                                Text(removeTrPrefix(child.categName))
                                                            }
                                                        },
                                                        onClick = {
                                                            viewModel.updateSplit(index, split.copy(categId = child.categId.toString()))
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Delete button
                                if (transactionUiState.splits.size > 1) {
                                    IconButton(onClick = { viewModel.removeSplitRow(index) }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Split")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.weight(1f),
                                    value = split.splitAmount,
                                    onValueChange = {
                                        viewModel.updateSplit(index, split.copy(splitAmount = it))
                                    },
                                    label = { Text("Amount *") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )

                                OutlinedTextField(
                                    modifier = Modifier.weight(1f),
                                    value = split.notes,
                                    onValueChange = {
                                        viewModel.updateSplit(index, split.copy(notes = it))
                                    },
                                    label = { Text("Notes") },
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.addSplitRow() }) {
                        Text("+ Add Split Item")
                    }

                    val splitSum = transactionUiState.splits.sumOf { it.splitAmount.toDoubleOrNull() ?: 0.0 }
                    val totalAmount = transactionUiState.transactionDetails.transAmount.toDoubleOrNull() ?: 0.0
                    Text(
                        text = "Sum of splits: ${String.format("%.2f", splitSum)} / ${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (Math.abs(totalAmount - splitSum) < 0.01) Color.Unspecified else MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            // Transaction Categories
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }) {
                    OutlinedTextField(
                        modifier = Modifier
                            .clickable(enabled = (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName)) {
                                categoryExpanded = true
                            }
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                        value = removeTrPrefix(currentCategory.categName),
                        readOnly = true,
                        onValueChange = {
                            viewModel.updateUiState(
                                transactionUiState.transactionDetails.copy(categoryId = it),
                                viewModel.transactionUiState.value.billsDepositsDetails,
                                currentAdvancedAmount
                            )
                        },
                        label = { Text("Transaction Category *") },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.moveFocus(
                                FocusDirection.Next
                            )
                        }),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        val parentCategories =
                            entityList.categoriesList.filter { it.parentId == -1 }
                        val childCategoriesMap =
                            entityList.categoriesList.filter { it.parentId != -1 }
                                .groupBy { it.parentId } // Group children by parentId

                        for (parent in parentCategories) {
                            DropdownMenuItem(text = {
                                Row {
                                    Text(removeTrPrefix(parent.categName))
                                }
                            }, onClick = {
                                currentCategory = parent
                                viewModel.updateUiState(
                                    transactionUiState.transactionDetails.copy(categoryId = parent.categId.toString()),
                                    viewModel.transactionUiState.value.billsDepositsDetails,
                                    currentAdvancedAmount
                                )
                                categoryExpanded = false
                            })

                            childCategoriesMap[parent.categId]?.forEach { child ->
                                DropdownMenuItem(text = {
                                    Row {
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(removeTrPrefix(child.categName))
                                    }
                                }, onClick = {
                                    currentCategory = child
                                    viewModel.updateUiState(
                                        transactionUiState.transactionDetails.copy(categoryId = child.categId.toString()),
                                        viewModel.transactionUiState.value.billsDepositsDetails,
                                        currentAdvancedAmount
                                    )
                                    categoryExpanded = false
                                })
                            }
                        }
                    }
                }
                IconButton(
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .padding(10.dp, 10.dp, 0.dp, 0.dp),
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
                                            viewModel.transactionUiState.value.billsDepositsDetails,
                                            currentAdvancedAmount
                                        )
                                    }
                                },
                                onEdit = { category ->
                                    coroutineScope.launch {
                                        entityViewModel.editCategory(category)
                                        viewModel.updateCategoriesList()
                                    }
                                },
                            )
                        )
                    },
                    enabled = (transactionUiState.transactionDetails.transCode != TransactionCode.TRANSFER.displayName)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Add"
                    )
                }
            }
        }

        /*        ExposedDropdownMenuBox(expanded = tagsExpanded, onExpandedChange = {
                    tagsExpanded = !tagsExpanded
                }) {
                    OutlinedTextField(modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .clickable(enabled = true) {
                            tagsExpanded = true
                        },
                        value = tagFilter,
                        readOnly = false,
                        onValueChange = { tagFilter = it },
                        label = { Text("Select a tag") },
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagsExpanded)
                        })

                    ExposedDropdownMenu(expanded = tagsExpanded, onDismissRequest = { }) {
                        if (filteredTags.isNotEmpty()) {
                            filteredTags.forEach { tag ->
                                tag?.let {
                                    DropdownMenuItem(text = { Text(tag.name) }, onClick = {
                                        // Check if the tag is already in the list
                                        if (!recipeTags.any { it.id == tag.id }) {
                                            // Add the new tag if it doesn't exist
                                            recipeTags = recipeTags + tag
                                        }
                                        tagFilter = ""
                                        tagsExpanded = false
                                    })
                                }
                            }
                        } else {
                            DropdownMenuItem(text = { Text("Add $tagFilter to database") },
                                onClick = {
                                    navController.navigate("Add Tag/$tagFilter")
                                })
                        }
                    }
                }
                LazyRow {
                    recipeTags.forEach { tag ->
                        item(key = tag.id) {
                            FilterChip(
                                modifier = Modifier.padding(end= 4.dp),
                                selected = true,  // Chips are not selected by default
                                onClick = {
                                    // Remove the tag from the list when clicked
                                    recipeTags = recipeTags.filter { it != tag }
                                }, label = {
                                    Text(text = tag.name)
                                }, trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,  // Close icon for the chip
                                        contentDescription = "Remove Tag"
                                    )
                                })
                        }
                    }
                }*/

        if (!showMore) {
            TextButton(onClick = { showMore = !showMore }) {
                Text(text = "Show more fields")
            }
        } else {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.transactionNumber,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(transactionNumber = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.notes,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(notes = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Notes") })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionUiState.transactionDetails.color,
                onValueChange = {
                    viewModel.updateUiState(
                        transactionUiState.transactionDetails.copy(color = it),
                        viewModel.transactionUiState.value.billsDepositsDetails,
                        currentAdvancedAmount
                    )
                },
                label = { Text("Color") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Receipt Attachments (${transactionUiState.attachments.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Gallery", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                fileLauncher.launch("*/*")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Choose File", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    if (transactionUiState.attachments.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            transactionUiState.attachments.forEach { path ->
                                val file = File(path)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = file.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeAttachment(path) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove attachment",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (openTransactionDateDialog) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openTransactionDateDialog = false
        },

            confirmButton = {
                TextButton(
                    onClick = {
                        openTransactionDateDialog = false
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = Date(datePickerState.selectedDateMillis!!)

                        viewModel.updateUiState(
                            transactionUiState.transactionDetails.copy(
                                transDate = dateFormat.format(
                                    date
                                )
                            ),
                            viewModel.transactionUiState.value.billsDepositsDetails,
                            currentAdvancedAmount
                        )
                    }, enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openTransactionDateDialog = false
                }) {
                    Text("Cancel")
                }
            }) {
            DatePicker(state = datePickerState)
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