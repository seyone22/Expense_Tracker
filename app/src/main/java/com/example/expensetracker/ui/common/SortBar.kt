package com.example.expensetracker.ui.common

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    periodSortAction: (Int) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableIntStateOf(0) }

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
                    Text(menuItems[selectedIndex])
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            // DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                menuItems.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex = index
                            expanded = false
                            // Perform Sort action
                            periodSortAction(selectedIndex)
                        },
                        text = { Text(text = item) }
                    )
                }
            }
        }
    }
}

val menuItems = listOf(
    "All",
    "Current Month",
    "Current Month to Date",
    "Last Month",
    "Last 30 Days",
    "Last 90 Days",
    "Last 3 Months",
    "Last 12 Months",
    "Current Year",
    "Current Year to Date",
    "Last Year",
    "Current Financial Year",
    "Current Financial Year to Date",
    "Last Financial Year",
    "Over Time",
    "Last 365 Days",
    "Custom"
)