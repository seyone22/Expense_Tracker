package com.seyone22.expensetracker.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    periodFilterAction: (FilterOption?) -> Unit,
    typeFilterAction: (TransactionCode?) -> Unit,
    statusFilterAction: (TransactionStatus?) -> Unit,
    accountFilterAction: (Account?) -> Unit,
    sortAction: (SortOption) -> Unit
) {
    // Code block to get a list of accounts.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var accountsList = emptyList<Account>()

    LaunchedEffect(sharedViewModel) {
        accountsList = sharedViewModel.accounts.firstOrNull()!!
    }

    var timePeriodExpanded by remember { mutableStateOf(false) }
    var selectedTimePeriodFilter by remember { mutableStateOf<FilterOption?>(null) }

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf<TransactionCode?>(null) }

    var statusExpanded by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<TransactionStatus?>(null) }

    var accountExpanded by remember { mutableStateOf(false) }
    var selectedAccountFilter by remember { mutableStateOf<Account?>(null) }

    var sortExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(SortOption.default) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Filter by Time Period
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                timePeriodExpanded = true
            }, selected = selectedTimePeriodFilter != null, label = {
                if (selectedTimePeriodFilter != null) {
                    Text(selectedTimePeriodFilter!!.displayName)
                } else {
                    Text("Time Period")
                }
            }, trailingIcon = {
                if (selectedTimePeriodFilter == null) {
                    Icon(
                        imageVector = if (typeExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            selectedTimePeriodFilter = null
                            periodFilterAction(selectedTimePeriodFilter)
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = timePeriodExpanded,
                onDismissRequest = { timePeriodExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                FilterOption.entries.forEachIndexed { index, filterOption ->
                    DropdownMenuItem(onClick = {
                        selectedTimePeriodFilter = filterOption
                        timePeriodExpanded = false
                        // Perform Sort action
                        periodFilterAction(filterOption)
                    }, text = { Text(text = filterOption.displayName) })
                }
            }
        }


        // Filter by Transaction Type

        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                typeExpanded = true
            }, selected = (selectedTypeFilter != null), label = {
                if (selectedTypeFilter != null) {
                    Text(selectedTypeFilter!!.displayName)
                } else {
                    Text("Type")
                }
            }, trailingIcon = {
                if (selectedTypeFilter == null) {
                    Icon(
                        imageVector = if (typeExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            selectedTypeFilter = null
                            typeFilterAction(selectedTypeFilter)
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                TransactionCode.entries.forEachIndexed { index, transactionCode ->
                    DropdownMenuItem(onClick = {
                        selectedTypeFilter = transactionCode
                        typeExpanded = false
                        typeFilterAction(transactionCode)
                    }, text = { Text(text = transactionCode.displayName) })
                }
            }
        }


        // Filter by Transaction Status
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                statusExpanded = true
            }, selected = (selectedStatusFilter != null), label = {
                if (selectedStatusFilter != null) {
                    Text(selectedStatusFilter!!.displayName)
                } else {
                    Text("Status")
                }
            }, trailingIcon = {
                if (selectedStatusFilter == null) {
                    Icon(
                        imageVector = if (statusExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            selectedStatusFilter = null
                            statusFilterAction(selectedStatusFilter)
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                TransactionStatus.entries.forEachIndexed { index, transactionStatus ->
                    DropdownMenuItem(onClick = {
                        selectedStatusFilter = transactionStatus
                        statusExpanded = false
                        // Perform Sort action
                        statusFilterAction(transactionStatus)
                    }, text = { Text(text = transactionStatus.displayName) })
                }
            }
        }

        // Filter by Account
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                accountExpanded = true
            }, selected = (selectedAccountFilter != null), label = {
                if (selectedAccountFilter != null) {
                    Text(selectedAccountFilter!!.accountName)
                } else {
                    Text("Account")
                }
            }, trailingIcon = {
                if (selectedAccountFilter == null) {
                    Icon(
                        imageVector = if (accountExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            selectedAccountFilter = null
                            accountFilterAction(selectedAccountFilter)
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = accountExpanded,
                onDismissRequest = { accountExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                accountsList.forEachIndexed { index, account ->
                    DropdownMenuItem(onClick = {
                        selectedAccountFilter = account
                        accountExpanded = false
                        // Perform Sort action
                        accountFilterAction(account)
                    }, text = { Text(text = account.accountName) })
                }
            }
        }


        // Sort
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                sortExpanded = true
            }, selected = true, label = {
                Text(selectedSort.displayName + " " + selectedSort.order)
            }, leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort"
                )
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                SortOption.options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedSort = if (selectedSort.key == option.key) {
                                selectedSort.toggleOrder()
                            } else {
                                option
                            }
                            sortExpanded = false
                            sortAction(selectedSort)
                        },
                        text = { Text(text = option.displayName) },
                    )
                }
            }
        }
    }
}

data class SortOption(val key: String, val order: String, val displayName: String) {
    fun toggleOrder(): SortOption {
        return copy(order = if (order == "ASC") "DESC" else "ASC")
    }

    companion object {
        val default = SortOption("transDate", "DESC", "Date")

        val options = listOf(
            SortOption("transDate", "ASC", "Date"),
            SortOption("payeeName", "ASC", "Payee"),
            SortOption("categName", "ASC", "Category"),
            SortOption("transAmount", "ASC", "Amount"),
            SortOption("transCode", "ASC", "Type"),
            SortOption("status", "ASC", "Status"),
            SortOption("account", "ASC", "Account")
        )
    }
}

enum class FilterOption(val displayName: String) {
    CURRENT_MONTH("Current Month"), CURRENT_MONTH_TO_DATE("Current Month to Date"), LAST_MONTH(
        "Last Month"
    ),
    LAST_30_DAYS("Last 30 Days"), LAST_90_DAYS("Last 90 Days"), LAST_3_MONTHS("Last 3 Months"), LAST_12_MONTHS(
        "Last 12 Months"
    ),
    CURRENT_YEAR("Current Year"), CURRENT_YEAR_TO_DATE("Current Year to Date"), LAST_YEAR("Last Year"), CURRENT_FINANCIAL_YEAR(
        "Current Financial Year"
    ),
    CURRENT_FINANCIAL_YEAR_TO_DATE("Current Financial Year to Date"), LAST_FINANCIAL_YEAR("Last Financial Year"), OVER_TIME(
        "Over Time"
    ),
    LAST_365_DAYS("Last 365 Days"), CUSTOM("Custom");

    companion object {
        fun fromString(value: String) = entries.find { it.displayName == value }
    }
}
