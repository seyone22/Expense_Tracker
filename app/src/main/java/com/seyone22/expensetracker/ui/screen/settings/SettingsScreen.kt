package com.seyone22.expensetracker.ui.screen.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.settings.panes.SettingsDetailPane
import com.seyone22.expensetracker.ui.screen.settings.panes.SettingsListPane
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SettingsDestination : NavigationDestination {
    override val route = "Settings"
    override val titleRes = R.string.app_name
    override val routeId = 14
    override val icon = Icons.Outlined.MoreHoriz
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    navigateBack: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    var activeScreen by remember { mutableStateOf("") }

    NavigableListDetailPaneScaffold(navigator = scaffoldNavigator, listPane = {
        AnimatedPane {
            SettingsListPane(
                setDetailPane = { key ->
                    coroutineScope.launch {
                        scaffoldNavigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail, contentKey = key
                        )
                    }
                }, navigateToScreen = navigateToScreen
            )
        }
    }, detailPane = {
        AnimatedPane {
            SettingsDetailPane(
                modifier = modifier,
                navigateBack = {
                    coroutineScope.launch {
                        scaffoldNavigator.navigateBack()
                    }
                },
                currentDestinationKey = scaffoldNavigator.currentDestination?.contentKey ?: "",
                navController = rememberNavController()
                )
        }
    })
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
