package com.example.expensetracker.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.expensetracker.R
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination

object SettingsDetailDestination : NavigationDestination {
    override val route = "SettingsDetail"
    override val titleRes = R.string.app_name
    override val routeId = 15
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    navigateBack : ()  -> Unit,
    backStackEntry: String,
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
                     title = { Text(text = backStackEntry) },
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
            when(backStackEntry) {
                "General" -> {
                    GeneralSettingsList()
                }
                "Appearance" -> {
                    GeneralSettingsList()
                }
                "About" -> {
                    AboutList()
                }
            }
        }
    }
}

@Composable
fun GeneralSettingsList() {
    Column {
        ListItem(
            headlineContent = { Text(text = "Username") },
            supportingContent = { Text(text = "current username") },
            modifier = Modifier.clickable {  }


        )
        ListItem(
            headlineContent = { Text(text = "Base Currency") },
            supportingContent = { Text(text = "LKR") },
            modifier = Modifier.clickable {  }

        )
    }
}

@Composable
fun AboutList() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
            .padding(0.dp,24.dp)
        ) {
        Icon(
            imageVector = Icons.Filled.CatchingPokemon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
    }
    HorizontalDivider()
    Column {
        ListItem(
            headlineContent = { Text(text = "Version") },
            supportingContent = { Text(text = "Alpha v0.1.1 (25/11/2023 : 00:00)") },
            modifier = Modifier.clickable {  }
        )
    }
}