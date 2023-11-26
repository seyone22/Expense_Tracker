package com.example.expensetracker.ui.screen.settings

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.R
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "Settings"
    override val titleRes = R.string.app_name
    override val routeId = 14
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    navigateBack : ()  -> Unit,
    //viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
                 TopAppBar(
                     colors = TopAppBarDefaults.topAppBarColors(
                         containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                         titleContentColor = MaterialTheme.colorScheme.onSurface,
                     ),
                     title = { Text(text = "Settings") },
                     navigationIcon = {
                         IconButton(onClick = { navigateBack() }) {
                             Icon(
                                 imageVector = Icons.Filled.ArrowBack,
                                 contentDescription = null
                             )
                         }

                     }
                 )
        },
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            ListItem(
                headlineContent = { Text(text = "General") },
                supportingContent = { Text(text = "Username, Base Currency") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Checklist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { navigateToScreen("SettingsDetail/General") }
            )
            ListItem(
                headlineContent = { Text(text = "Appearance") },
                supportingContent = { Text(text = "Theme, date & time formats") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { navigateToScreen("SettingsDetail/Appearance") }
            )
            ListItem(
                headlineContent = { Text(text = "About") },
                supportingContent = { Text(text = "Expense Tracker v0.1.0-alpha") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { navigateToScreen("SettingsDetail/About") }
            )
        }
    }
}