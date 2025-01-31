package com.seyone22.expensetracker.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortBar(
    modifier: Modifier = Modifier, periodSortAction: (FilterOption) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedFilterOption by remember { mutableStateOf(FilterOption.ALL) }

        Column {
            // TextButton with an icon
            TextButton(
                onClick = {
                    expanded = true
                },
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.padding(8.dp, 4.dp, 0.dp, 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(selectedFilterOption.displayName)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            // DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                FilterOption.entries.forEachIndexed { index, filterOption ->
                    DropdownMenuItem(onClick = {
                        selectedFilterOption = filterOption
                        expanded = false
                        // Perform Sort action
                        periodSortAction(filterOption)
                    }, text = { Text(text = filterOption.displayName) })
                }
            }
        }
    }
}

enum class FilterOption(val displayName: String) {
    ALL("All"), CURRENT_MONTH("Current Month"), CURRENT_MONTH_TO_DATE("Current Month to Date"), LAST_MONTH(
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
        fun fromString(value: String) = entries.find { it.displayName == value } ?: ALL
    }
}
