package com.seyone22.expensetracker.ui.screen.settings.panes

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
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.screen.settings.SettingsListItem

@Composable
fun SettingsListPane(
    setDetailPane: (screen: String) -> Unit,
    navigateToScreen: (screen: String) -> Unit,
) {
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
            action = { setDetailPane("General") })
        SettingsListItem(settingName = "Appearance",
            settingSubtext = "Theme, date & time formats",
            settingIcon = Icons.Outlined.Palette,
            action = { setDetailPane("Appearance") })
        SettingsListItem(settingName = "Fetch Data",
            settingSubtext = "Update Exchange Rates",
            settingIcon = Icons.Outlined.Download,
            action = { setDetailPane("Data") })
        SettingsListItem(settingName = "Privacy and Security",
            settingSubtext = "App lock, Secure Screen",
            settingIcon = Icons.Outlined.Security,
            action = { setDetailPane("Security") })
        SettingsListItem(settingName = "Import & Export",
            settingSubtext = "Manage your transaction data",
            settingIcon = Icons.Outlined.ImportExport,
            action = { setDetailPane("ImportExport") })
        SettingsListItem(settingName = "About",
            settingSubtext = "${stringResource(id = R.string.app_name)} ${
                stringResource(
                    id = R.string.app_version
                )
            }",
            settingIcon = Icons.Outlined.Info,
            action = { setDetailPane("About") })
    }
}