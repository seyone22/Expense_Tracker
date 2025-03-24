package com.seyone22.expensetracker.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.MainNavigationDestinations
import com.seyone22.expensetracker.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "Settings"
    override val titleRes = R.string.app_name
    override val routeId = 14
    override val icon = Icons.Outlined.MoreHoriz
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentDestination by rememberSaveable { mutableStateOf(MainNavigationDestinations.MORE) }

    Column(
        Modifier.windowInsetsPadding(insets = WindowInsets.statusBars)
    ) {
        ExpenseTopBar(
            selectedActivity = "More",
            type = "Center",
            hasNavBarAction = false,
            navController = rememberNavController()
        )

        SettingsListItem(settingName = "Entities",
            settingSubtext = "View & Manage Entities",
            settingIcon = Icons.Outlined.Bookmark,
            action = { navigateToScreen("Entities") })
        HorizontalDivider(
            modifier = Modifier, thickness = 1.dp, color = Color.Gray
        )
        SettingsListItem(settingName = "General",
            settingSubtext = "Username, Base Currency",
            settingIcon = Icons.Outlined.Checklist,
            action = { navigateToScreen("SettingsDetail/General") })
        SettingsListItem(settingName = "Appearance",
            settingSubtext = "Theme, date & time formats",
            settingIcon = Icons.Outlined.Palette,
            action = { navigateToScreen("SettingsDetail/Appearance") })
        SettingsListItem(settingName = "Fetch Data",
            settingSubtext = "Update Exchange Rates",
            settingIcon = Icons.Outlined.Download,
            action = { navigateToScreen("SettingsDetail/Data") })
        SettingsListItem(settingName = "Privacy and Security",
            settingSubtext = "App lock, Secure Screen",
            settingIcon = Icons.Outlined.Security,
            action = { navigateToScreen("SettingsDetail/Security") })
        SettingsListItem(settingName = "Import & Export",
            settingSubtext = "Manage your transaction data",
            settingIcon = Icons.Outlined.ImportExport,
            action = { navigateToScreen("SettingsDetail/ImportExport") })
        SettingsListItem(settingName = "About",
            settingSubtext = "${stringResource(id = R.string.app_name)} ${
                stringResource(
                    id = R.string.app_version
                )
            }",
            settingIcon = Icons.Outlined.Info,
            action = { navigateToScreen("SettingsDetail/About") })
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
    onToggleChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    var tx by remember { mutableStateOf(toggle) }
    ListItem(modifier = modifier.clickable(onClick = {
        if (enabled) {
            tx = !tx
            onToggleChange(tx)
        }
    }), headlineContent = { Text(text = settingName) }, supportingContent = {
        if (settingSubtext != null) {
            Text(text = settingSubtext)
        }
    }, leadingContent = {
        if (settingIcon != null) {
            Icon(
                imageVector = settingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }, trailingContent = {
        Switch(
            checked = tx, onCheckedChange = {
                tx = !tx
                onToggleChange(tx)
            }, enabled = enabled
        )
    })
}
