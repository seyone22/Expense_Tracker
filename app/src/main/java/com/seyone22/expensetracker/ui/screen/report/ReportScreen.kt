package com.seyone22.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.report.composables.ReportCard

object ReportsDestination : NavigationDestination {
    override val route = "Reports"
    override val titleRes = R.string.app_name
    override val routeId = 4
    override val icon = Icons.AutoMirrored.Outlined.TextSnippet
}

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: ReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val reports by viewModel.reportsFlow.collectAsState(initial = emptyList())

    ExpenseTopBar(
        selectedActivity = ReportsDestination.route,
        type = "Center",
        navController = rememberNavController()
    )

    LazyVerticalGrid(
        modifier = Modifier.windowInsetsPadding(insets = WindowInsets.statusBars),
        columns = GridCells.Adaptive(minSize = 320.dp)
    ) {
        items(reports.size) { index ->
            ReportCard(
                modifier = Modifier, viewModel = viewModel, report = reports[index]
            )
        }
    }
}
