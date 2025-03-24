package com.seyone22.expensetracker.ui.screen.entities

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.entities.panes.EntityDetailPane
import com.seyone22.expensetracker.ui.screen.entities.panes.EntityListPane

object EntitiesDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
    override val routeId = 1
    override val icon = null
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntityScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()

    NavigableListDetailPaneScaffold(navigator = scaffoldNavigator, listPane = {
        AnimatedPane {
            EntityListPane(
                modifier,
                viewModel,
                navController = navController,
                scaffoldNavigator = scaffoldNavigator
            )
        }
    }, detailPane = {
        AnimatedPane {
            EntityDetailPane(
                viewModel, scaffoldNavigator = scaffoldNavigator, navController = navController
            )
        }
    })
}


