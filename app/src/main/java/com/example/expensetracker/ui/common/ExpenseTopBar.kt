package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.expensetracker.ui.screen.onboarding.OnboardingDestination
import com.example.expensetracker.ui.screen.operations.account.AccountDetailDestination
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.operations.report.ReportEntryDestination
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.utils.ExpenseNavigationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopBar(
    selectedActivity: String?,
    navBarAction: () -> Unit,
    type: ExpenseNavigationType,
    hasNavBarAction: Boolean = true,
    navController: NavController,
    navigateToSettings: () -> Unit
) {
    //Title string for header elements, view codes in NavigationDestinations
    val titleString: String = (selectedActivity ?: "Expenses").split(("/")).first()

    if ((selectedActivity != null) and (selectedActivity != AccountEntryDestination.route) and (selectedActivity != SettingsDestination.route) and (selectedActivity != "SettingsDetail/{setting}") and (selectedActivity != TransactionEntryDestination.route) and (selectedActivity != ReportEntryDestination.route) and (selectedActivity != OnboardingDestination.route)) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            title = {
                Text(titleString)
            },
            navigationIcon = {
                if (selectedActivity!!.contains(AccountDetailDestination.route)) { // Null check performed at the very top
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                } else {
                    if (type == ExpenseNavigationType.BOTTOM_NAVIGATION) {
                        IconButton(onClick = {
                            navigateToSettings()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }

            },
            actions = {
                if (hasNavBarAction) {
                    IconButton(onClick = { navBarAction() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Item"
                        )
                    }
                }
            }
        )
    }
}