package com.seyone22.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopBar(
    selectedActivity: String?,
    navBarAction: () -> Unit = {},
    type: String = "Left",
    hasNavBarAction: Boolean = true,
    navController: NavController,
    navigateToSettings: () -> Unit = {}
) {
    //Title string for header elements, view codes in NavigationDestinations
    val titleString: String = (selectedActivity ?: "Expenses").split(("/")).first()

    if (type == "Center") {
        CenterAlignedTopAppBar(title = {
            Text(titleString)
        }, navigationIcon = {
            if (selectedActivity!!.contains(AccountDetailDestination.route)) { // Null check performed at the very top
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            } else {
                if (true) {
                    IconButton(onClick = {
                        navigateToSettings()
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
        TopAppBar(
            title = {
                Text(titleString)
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            },
        )
    }
}