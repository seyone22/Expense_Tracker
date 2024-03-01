package com.example.expensetracker.ui.screen.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.DonutChart
import com.example.expensetracker.ui.common.DonutChartData
import com.example.expensetracker.ui.common.DonutChartDataCollection
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.navigation.NavigationDestination

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
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(count = 2) {
            Column {
                Text(
                    text = "Transactions by Payees",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                TextButtonWithMenu()
                ReportByPayee(viewModel)
            }
            Column {
                Text(
                    text = "Transactions by Categories",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                TextButtonWithMenu()
                ReportByCategory(viewModel)
            }
        }
    }
}

@Composable
fun ReportByPayee(
    viewModel: ReportViewModel
) {
    // Collect the flow and automatically recompose when the data changes
    val byPayeeData by viewModel.byPayeeData.collectAsState(
        initial = Pair(emptyList(), emptyList())
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 12.dp),

        ) {
        DonutChart(
            modifier = Modifier
                .height(300.dp)
                .width(268.dp)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            data = DonutChartDataCollection(
                byPayeeData.second.mapIndexed { index, amount ->
                    DonutChartData(
                        amount.toFloat(),
                        viewModel.generateDistinctColors(byPayeeData.second.size)[index],
                        byPayeeData.first[index].payeeName
                    )
                }
            )
        ) { selected ->
            AnimatedContent(targetState = selected, label = "") {
                if (it != null) {
                    Column(modifier = Modifier.width(100.dp)) {
                        Text(
                            text = it.title,
                            textAlign = TextAlign.Center
                        )
                        FormattedCurrency(
                            value = (it.amount).toDouble(),
                            currency = CurrencyFormat() //FIX THIS
                        )
                    }
                }
            }
        }
        Text(
            text = "Expense",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ReportByCategory(
    viewModel: ReportViewModel
) {
    // Collect the flow and automatically recompose when the data changes
    val byCategoryData by viewModel.byCategoryData.collectAsState(
        initial = Pair(emptyList(), emptyList())
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 12.dp),

        ) {
        DonutChart(
            modifier = Modifier
                .height(300.dp)
                .width(268.dp)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            data = DonutChartDataCollection(
                byCategoryData.second.mapIndexed { index, amount ->
                    DonutChartData(
                        amount.toFloat(),
                        viewModel.generateDistinctColors(byCategoryData.second.size)[index],
                        byCategoryData.first[index].categName
                    )
                }
            )
        ) { selected ->
            AnimatedContent(targetState = selected, label = "") {
                if (it != null) {
                    Column(modifier = Modifier.width(100.dp)) {
                        Text(
                            text = it.title,
                            textAlign = TextAlign.Center
                        )
                        FormattedCurrency(
                            value = (it.amount).toDouble(),
                            currency = CurrencyFormat() //FIX THIS
                        )
                    }
                }
            }
        }
        Text(
            text = "Expense",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
fun TextButtonWithMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val menuItems = listOf(
        "Current Month",
        "Current Month to Date",
        "Last Month",
        "Last 30 Days",
        "Last 90 Days",
        "Last 3 Months",
        "Last 12 Months",
        "Current Year",
        "Current Year to Date",
        "Last Year",
        "Current Financial Year",
        "Current Financial Year to Date",
        "Last Financial Year",
        "Over Time",
        "Last 365 Days",
        "Custom"
    )

    //val context = LocalContext.current

    Column {
        // TextButton with an icon
        TextButton(
            onClick = {
                expanded = true
            },
            contentPadding = PaddingValues(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(menuItems[selectedIndex])
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        // DropdownMenu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            menuItems.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                    },
                    text = { Text(text = item) }
                )
            }
        }
    }
}