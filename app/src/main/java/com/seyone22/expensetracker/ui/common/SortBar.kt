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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionFilters
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    // Code block to get a list of accounts.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var accountsList = emptyList<Account>()
    var payeesList = emptyList<Payee>()
    var categoriesList = emptyList<Category>()

    LaunchedEffect(Unit) {
        accountsList = sharedViewModel.accountsFlow.firstOrNull()!!
        payeesList = sharedViewModel.payeesFlow.firstOrNull()!!
        categoriesList = sharedViewModel.categoriesFlow.firstOrNull()!!
    }

    val filters by viewModel.filters.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    // Dropdown expansion states
    var timePeriodExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var sortExpanded by remember { mutableStateOf(false) }

    fun onFilterChange(newFilters: TransactionFilters) {
        viewModel.setFilters(newFilters)
    }

    // Sort options UI
    fun onSortChange(newSortOption: SortOption) {
        viewModel.setSortOption(newSortOption)
    }

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
            }, selected = filters.timeFilter != null, label = {
                if (filters.timeFilter != null) {
                    Text(filters.timeFilter!!.displayName)
                } else {
                    Text("Time Period")
                }
            }, trailingIcon = {
                if (filters.timeFilter == null) {
                    Icon(
                        imageVector = if (typeExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(timeFilter = null))
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
                TimeRangeFilter.entries.forEachIndexed { index, filterOption ->
                    DropdownMenuItem(onClick = {
                        timePeriodExpanded = false
                        // Perform Sort action
                        onFilterChange(filters.copy(timeFilter = filterOption))
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
            }, selected = (filters.typeFilter != null), label = {
                if (filters.typeFilter != null) {
                    Text(filters.typeFilter!!.displayName)
                } else {
                    Text("Type")
                }
            }, trailingIcon = {
                if (filters.typeFilter == null) {
                    Icon(
                        imageVector = if (typeExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(typeFilter = null))
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
                        typeExpanded = false
                        onFilterChange(filters.copy(typeFilter = transactionCode))
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
            }, selected = (filters.statusFilter != null), label = {
                if (filters.statusFilter != null) {
                    Text(filters.statusFilter!!.displayName)
                } else {
                    Text("Status")
                }
            }, trailingIcon = {
                if (filters.statusFilter == null) {
                    Icon(
                        imageVector = if (statusExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(statusFilter = null))
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
                        statusExpanded = false
                        // Perform Sort action
                        onFilterChange(filters.copy(statusFilter = transactionStatus))
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
            }, selected = (filters.accountFilter != null), label = {
                if (filters.accountFilter != null) {
                    Text(filters.accountFilter!!.accountName)
                } else {
                    Text("Account")
                }
            }, trailingIcon = {
                if (filters.accountFilter == null) {
                    Icon(
                        imageVector = if (accountExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(accountFilter = null))
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
                        accountExpanded = false
                        // Perform Sort action
                        onFilterChange(filters.copy(accountFilter = account))
                    }, text = { Text(text = account.accountName) })
                }
            }
        }

        // Filter by Payee
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                payeeExpanded = true
            }, selected = (filters.payeeFilter != null), label = {
                if (filters.payeeFilter != null) {
                    Text(filters.payeeFilter!!.payeeName)
                } else {
                    Text("Payee")
                }
            }, trailingIcon = {
                if (filters.payeeFilter == null) {
                    Icon(
                        imageVector = if (payeeExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(payeeFilter = null))
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = payeeExpanded,
                onDismissRequest = { payeeExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                payeesList.forEachIndexed { index, payee ->
                    DropdownMenuItem(onClick = {
                        payeeExpanded = false
                        // Perform Sort action
                        onFilterChange(filters.copy(payeeFilter = payee))
                    }, text = { Text(text = payee.payeeName) })
                }
            }
        }

        // Filter by Category
        Column(
            modifier = Modifier,
        ) {
            // TextButton with an icon
            FilterChip(onClick = {
                categoryExpanded = true
            }, selected = (filters.categoryFilter != null), label = {
                if (filters.categoryFilter != null) {
                    Text(filters.categoryFilter!!.categName)
                } else {
                    Text("Category")
                }
            }, trailingIcon = {
                if (filters.categoryFilter == null) {
                    Icon(
                        imageVector = if (categoryExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort"
                    )
                } else {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Sort",
                        modifier = Modifier.clickable {
                            onFilterChange(filters.copy(categoryFilter = null))
                        })
                }
            }, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )

            // DropdownMenu
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                categoriesList.forEachIndexed { index, category ->
                    DropdownMenuItem(onClick = {
                        categoryExpanded = false
                        // Perform Sort action
                        onFilterChange(filters.copy(categoryFilter = category))
                    }, text = { Text(text = category.categName) })
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
                Text(sortOption.displayName + " " + sortOption.order)
            }, leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort"
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
                            if (sortOption.key == option.key) {
                                onSortChange(option.toggleOrder())
                            } else {
                                onSortChange(option)
                            }
                            sortExpanded = false
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
            SortOption("account", "ASC", "Account"),
            SortOption("payee", "ASC", "Payee"),
            SortOption("category", "ASC", "Category"),
        )
    }
}

enum class TimeRangeFilter(val displayName: String) {
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
