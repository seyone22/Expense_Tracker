package com.seyone22.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopBar(
    selectedActivity: String?,
    type: String = "Left",
    hasNavBarAction: Boolean = true,
    navBarAction: () -> Unit = {},
    navController: NavController,
    hasNavigation: Boolean = false,
    dropdownOptions: List<Pair<String, () -> Unit>> = listOf() // Accept a list of options and their corresponding actions
) {
    val titleString: String = (selectedActivity ?: "Expenses").split("/").first()

    // State to control the dropdown menu visibility
    var isDropdownExpanded by remember { mutableStateOf(false) }

    if (type == "Center") {
        CenterAlignedTopAppBar(title = {
            Text(titleString)
        }, navigationIcon = {
            if (selectedActivity!!.contains(AccountDetailDestination.route)) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            } else {
                if (hasNavigation) {
                    IconButton(onClick = {
                        navController.navigate(SettingsDestination.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings, contentDescription = "Settings"
                        )
                    }
                }
            }

        }, actions = {
            if (hasNavBarAction) {
                IconButton(onClick = { navBarAction() }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Add Item"
                    )
                }
            }
        })
    } else if (type == "Left") {
        TopAppBar(title = { Text(titleString) }, navigationIcon = {
            if (hasNavigation) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        }, actions = {
            if (hasNavBarAction) {
                // Dropdown button
                IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert, contentDescription = "More Options"
                    )
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    dropdownOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.first) },
                            onClick = {
                                option.second() // Call the function associated with the option
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        })
    }
}