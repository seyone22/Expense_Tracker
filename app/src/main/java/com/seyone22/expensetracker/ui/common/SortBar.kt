package com.seyone22.expensetracker.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus

@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    periodSortAction: (FilterOption?) -> Unit,
    typeSortAction: (TransactionCode?) -> Unit,
    statusSortAction: (TransactionStatus?) -> Unit
) {
    var timePeriodExpanded by remember { mutableStateOf(false) }
    var selectedTimePeriodFilter by remember { mutableStateOf<FilterOption?>(null) }

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf<TransactionCode?>(null) }

    var statusExpanded by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<TransactionStatus?>(null) }

    LazyRow(
        modifier = modifier.fillMaxWidth()
    ) {


        // Filter by Time Period
        item {
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
                                periodSortAction(selectedTimePeriodFilter)
                            })
                    }
                }, modifier = Modifier.padding(8.dp, 4.dp, 0.dp, 4.dp)
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
                            periodSortAction(filterOption)
                        }, text = { Text(text = filterOption.displayName) })
                    }
                }
            }
        }

        // Filter by Transaction Type
        item {
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
                                typeSortAction(selectedTypeFilter)
                            })
                    }
                }, modifier = Modifier.padding(8.dp, 4.dp, 0.dp, 4.dp)
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
                            // Perform Sort action
                            typeSortAction(transactionCode)
                        }, text = { Text(text = transactionCode.displayName) })
                    }
                }
            }
        }

        // Filter by Transaction Status
        item {
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
                                statusSortAction(selectedStatusFilter)
                            })
                    }
                }, modifier = Modifier.padding(8.dp, 4.dp, 0.dp, 4.dp)
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
                            statusSortAction(transactionStatus)
                        }, text = { Text(text = transactionStatus.displayName) })
                    }
                }
            }
        }
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
