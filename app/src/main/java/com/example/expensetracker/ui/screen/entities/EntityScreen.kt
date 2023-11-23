package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination

object EntitiesDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
    override val routeId = 1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Categories", "Payees", "Currencies")
    //TODO: Refactor this to be more elegant
    val entityUiState : EntitiesUiState by viewModel.entitiesUiState.collectAsState()
    val entityUiState2 : EntitiesUiState by viewModel.entitiesUiState2.collectAsState()
    val entityUiState3 : EntitiesUiState by viewModel.entitiesUiState3.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ExpenseTopBar(
                selectedActivity = EntitiesDestination.routeId,
                navBarAction = {
                    when(state) {
                        0 -> {
                            navigateToScreen(CategoryEntryDestination.route)
                        }
                        1 -> {
                            navigateToScreen(PayeeEntryDestination.route)
                        }
                        2 -> {
                            navigateToScreen(CurrencyEntryDestination.route)
                        }
                    }
                },
                navigateToSettings = { navigateToScreen(SettingsDestination.route) }
            )
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = EntitiesDestination.routeId, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
                PrimaryTabRow(
                    selectedTabIndex = state,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                        )

                    }
                }
                when(state) {
                    0 -> {
                        CategoryList(list = entityUiState.categoriesList)
                    }
                    1 -> {
                        PayeeList(list = entityUiState3.payeesList)
                        Log.d("DEBUG", "EntityScreen: $entityUiState3")
                    }
                    2 -> {
                        CurrenciesList(list = entityUiState2.currenciesList)
                    }
                }
            }

    }
}


@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    list: List<Category>
) {
    LazyColumn() {
        items(list) {
            ListItem(
                headlineContent = { Text(it.categName) },
                overlineContent = { if(it.parentId != -1) { Text(it.parentId.toString())} else { Text("") } },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                    )
                },
                trailingContent = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = { /*TODO*/ },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = null,
                                Modifier.size(36.dp, 36.dp)
                            )
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun PayeeList(
    modifier: Modifier = Modifier,
    list: List<Payee>
) {
    LazyColumn() {
        items(list) {
            ListItem(
                headlineContent = { Text(it.payeeName.toString()) },
                overlineContent = { Text(it.payeeId.toString()) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                    )
                },
                trailingContent = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = { /*TODO*/ },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = null,
                                Modifier.size(36.dp, 36.dp)
                            )
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun CurrenciesList(
    modifier: Modifier = Modifier,
    list: List<CurrencyFormat>
) {
    LazyColumn() {
        items(list) {
            ListItem(
                headlineContent = { Text(it.baseConvRate.toString()) },
                overlineContent = { Text(it.currencyName) },
                leadingContent = { Text(it.currency_symbol) },
                trailingContent = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = { /*TODO*/ },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = null,
                                Modifier.size(36.dp, 36.dp)
                            )
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}