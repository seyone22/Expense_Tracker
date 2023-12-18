package com.example.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.AnimatedCircle
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination

object ReportsDestination : NavigationDestination {
    override val route = "Reports"
    override val titleRes = R.string.app_name
    override val routeId = 4
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ExpenseTopBar(
                selectedActivity = ReportsDestination.routeId,
                navBarAction = {  },
                navigateToSettings = { navigateToScreen(SettingsDestination.route) }
            )
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = ReportsDestination.routeId, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            ReportByCategory()
        }
    }
}

@Composable
fun ReportByCategory() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 12.dp)
    ) {
        AnimatedCircle(
            proportions = listOf(0.2f, 0.3f, 0.4f, 0.1f),
            colors = listOf(Color.Green, Color.Red, Color.Blue, Color.Yellow),
            modifier = Modifier
                .height(300.dp)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        )
    }
}