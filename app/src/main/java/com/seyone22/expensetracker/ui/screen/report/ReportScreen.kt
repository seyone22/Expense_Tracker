package com.seyone22.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.report.composables.ReportCard

object ReportsDestination : NavigationDestination {
    override val route = "Reports"
    override val titleRes = R.string.app_name
    override val routeId = 4
}

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: ReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val reports by viewModel.reportsFlow.collectAsState(initial = listOf())

    Scaffold(bottomBar = {
        ExpenseNavBar(
            currentActivity = ReportsDestination.route, navigateToScreen = navigateToScreen
        )
    }, topBar = {
        ExpenseTopBar(
            selectedActivity = ReportsDestination.route,
            navController = rememberNavController(),
            type = "Center"
        )
    }) {
        LazyVerticalGrid(
            modifier = Modifier.padding(paddingValues = it),
            columns = GridCells.Adaptive(minSize = 320.dp)
        ) {
            items(reports.size) { index ->
                ReportCard(
                    modifier = Modifier, viewModel = viewModel, report = reports[index]
                )
            }
        }
    }
}
