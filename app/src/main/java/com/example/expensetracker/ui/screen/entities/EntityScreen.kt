package com.example.expensetracker.ui.screen.entities

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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

object EntitiesDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityScreen(
    navigateToEntityEntry: () -> Unit,
    navigateToScreen: (screen: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Categories", "Payees", "Currencies")
    val entityUiState : EntitiesUiState by viewModel.entitiesUiState.collectAsState()

    Scaffold(
        topBar = {
            ExpenseTopBar(selectedActivity = 1)
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = 1, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
                PrimaryTabRow(selectedTabIndex = state) {
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
                        PayeeList(list = entityUiState.payeesList)
                    }
                    2 -> {
                        CurrenciesList(list = entityUiState.currenciesList)
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
                headlineContent = { Text("Three line list item") },
                overlineContent = { Text("OVERLINE") },
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
                headlineContent = { Text("Three line list item") },
                overlineContent = { Text("OVERLINE") },
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








/*
@Composable
fun CategoryCard(
    entityWithBalance: Pair<Payee, Double>,
    modifier: Modifier = Modifier,
    viewModel : EntityViewModel
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .padding(0.dp, 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {
                Icon(
                    imageVector = Icons.Outlined.EntityBalanceWallet,
                    contentDescription = null,
                    Modifier.size(36.dp, 36.dp)
                )
            }
            Column(
                Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = entityWithBalance.first.entityName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = entityWithBalance.first.status,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Column(
                Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 12.dp, 0.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Rs. "+entityWithBalance.second.toString()
                )
                Text(
                    text = "Rs. "+entityWithBalance.second.toString()
                )
            }
        }
    }
}*/
