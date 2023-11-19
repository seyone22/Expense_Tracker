package com.example.expensetracker.ui.screen.entities

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.activitiesAndIcons
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.transaction.TransactionEntryScreen

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
    var selectedActivity by remember { mutableIntStateOf(1) }
    val entityUiState by viewModel.entitiesUiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ExpenseTopBar(selectedActivity = selectedActivity)
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = selectedActivity, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.padding(innerPadding)
        ) {
            item {
                /*Text("Current Month Summary")
                Text(text = entityUiState.grandTotal.toString())
                Text("Your Entitys")
                enumValues<EntityTypes>().forEach { entityType ->
                    if(viewModel.countInType(entityType, entityUiState.entityList) != 0) {
                        val displayName: String = entityType.displayName
                        EntityList(
                            category = displayName,
                            entityList = entityUiState.entityList,
                            viewModel = viewModel
                        )
                    }
                }
                Button(onClick = { navigateToScreen(EntityEntryDestination.route) }) {
                    Text(text = "New Entity")
                }*/
            }
        }
    }
}
/*

@Composable
fun EntityList(
    category: String,
    entityList: List<Pair<Payee,Double>>,
    modifier: Modifier = Modifier,
    viewModel: EntityViewModel
) {
    Column(
        Modifier.padding(16.dp, 12.dp),

    ) {
        Text(text = category, style = MaterialTheme.typography.titleLarge)
        Column(modifier = modifier) {
            entityList.forEach { entityPair ->
                Log.d("DEBUG", "EntityList: Ping")
                if(entityPair.first.entityType == category) {
                    EntityCard(
                        entityWithBalance = entityPair,
                        modifier = Modifier,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun EntityCard(
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
