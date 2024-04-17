package com.example.expensetracker.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
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
    navigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            SettingsListItem(
                settingName = "General",
                settingSubtext = "Username, Base Currency",
                settingIcon = Icons.Outlined.Checklist,
                action = { navigateToScreen("SettingsDetail/General") }
            )
            SettingsListItem(
                settingName = "Appearance",
                settingSubtext = "Theme, date & time formats",
                settingIcon = Icons.Outlined.Palette,
                action = { navigateToScreen("SettingsDetail/Appearance") }
            )
            SettingsListItem(
                settingName = "Fetch Data",
                settingSubtext = "Update Exchange Rates",
                settingIcon = Icons.Outlined.Palette,
                action = { navigateToScreen("SettingsDetail/Data") }
            )
            SettingsListItem(
                settingName = "Privacy and Security",
                settingSubtext = "App lock, Secure Screen",
                settingIcon = Icons.Outlined.Security,
                action = { navigateToScreen("SettingsDetail/Security") }
            )
            SettingsListItem(
                settingName = "Import & Export",
                settingSubtext = "Manage your transaction data",
                settingIcon = Icons.Outlined.ImportExport,
                action = { navigateToScreen("SettingsDetail/ImportExport") }
            )
            SettingsListItem(
                settingName = "About",
                settingSubtext = "${stringResource(id = R.string.app_name)} ${
                    stringResource(
                        id = R.string.app_version
                    )
                }",
                settingIcon = Icons.Outlined.Info,
                action = { navigateToScreen("SettingsDetail/About") }
            )
        }
    }
}

@Composable
fun SettingsListItem(
    modifier: Modifier = Modifier,
    settingName: String,
    settingSubtext: String,
    settingIcon: ImageVector? = null,
    toggle: Boolean = false,
    action: () -> Unit
) {
    ListItem(
        modifier = modifier.clickable(onClick = action),
        headlineContent = { Text(text = settingName) },
        supportingContent = { Text(text = settingSubtext) },
        leadingContent = {
            if (settingIcon != null) {
                Icon(
                    imageVector = settingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    )
}

@Composable
fun SettingsToggleListItem(
    modifier: Modifier = Modifier,
    settingName: String,
    settingSubtext: String? = null,
    settingIcon: ImageVector? = null,
    toggle: Boolean = false,
    onToggleChange: (Boolean) -> Unit
) {
    var tx by remember { mutableStateOf(toggle) }
    ListItem(
        modifier = modifier.clickable(onClick = {
            tx = !tx
            onToggleChange(tx) }),
        headlineContent = { Text(text = settingName) },
        supportingContent = {
            if (settingSubtext != null) {
                Text(text = settingSubtext)
            }
        },
        leadingContent = {
            if (settingIcon != null) {
                Icon(
                    imageVector = settingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingContent = {
            Switch(checked = tx, onCheckedChange = {
                tx = !tx
                onToggleChange(tx)
            })
        }
    )
}
