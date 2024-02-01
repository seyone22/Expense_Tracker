package com.example.expensetracker.ui.screen.entities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.DeleteConfirmationDialog
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryDetails
import com.example.expensetracker.ui.screen.operations.entity.category.toCategoryDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.toCurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayeeDetails
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()

    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Categories", "Payees", "Currencies")

    val entityUiState: EntitiesUiState by viewModel.entitiesUiState.collectAsState(EntitiesUiState())

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showPayeeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ExpenseTopBar(
                selectedActivity = EntitiesDestination.routeId,
                navBarAction = {
                    when (state) {
                        0 -> {
                            showCategoryDialog = true
                        }

                        1 -> {
                            showPayeeDialog = true
                        }

                        2 -> {
                            showCurrencyDialog = true
                        }
                    }
                },
                navigateToSettings = { navigateToScreen(SettingsDestination.route) }
            )
        },
        bottomBar = {
            ExpenseNavBar(
                selectedActivity = EntitiesDestination.routeId,
                navigateToScreen = navigateToScreen
            )
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = state,
                containerColor = MaterialTheme.colorScheme.background,

                ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )

                }
            }
            when (state) {
                0 -> {
                    CategoryList(
                        list = entityUiState.categoriesList,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope
                    )
                }

                1 -> {
                    PayeeList(
                        list = entityUiState.payeesList,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope
                    )
                }

                2 -> {
                    CurrenciesList(
                        list = entityUiState.currenciesList,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope
                    )
                }
            }
        }

    }

    if (showCategoryDialog) {
        CategoryEntryDialog(
            onDismissRequest = { showCategoryDialog = false },
            viewModel = viewModel,
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.saveCategory()

                }
            }
        )
    }
    if (showPayeeDialog) {
        PayeeEntryDialog(
            onDismissRequest = { showPayeeDialog = false },
            viewModel = viewModel,
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.savePayee()
                }
            }
        )
    }
    if (showCurrencyDialog) {
        CurrencyEntryDialog(
            onDismissRequest = { showCurrencyDialog = false },
            viewModel = viewModel,
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.saveCurrency()
                }
            }
        )
    }
}


@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    list: List<Category>,
    viewModel: EntityViewModel,
    coroutineScope: CoroutineScope
) {
    LazyColumn() {
        items(list, { item -> item.categId } ) {
            ListItem(
                headlineContent = { Text( removeTrPrefix(it.categName)) },
                overlineContent = {
                    if (it.parentId != -1) {
                        Text(it.parentId.toString())
                    } else {
                        Text("")
                    }
                },
                leadingContent = {
                    Icon(
                        Icons.Filled.Bookmark,
                        contentDescription = "Localized description",
                    )
                },
                trailingContent = {
                    val openDeleteAlertDialog = remember { mutableStateOf(false) }
                    val openEditDialog = remember { mutableStateOf(false) }
                    val moreExpanded = remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            moreExpanded.value = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = modifier.size(36.dp, 36.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = moreExpanded.value,
                        onDismissRequest = { moreExpanded.value = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Edit") },
                            onClick = {
                                openEditDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Delete") },
                            onClick = {
                                openDeleteAlertDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                    }

                    if (openDeleteAlertDialog.value) {
                        DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                            coroutineScope.launch {
                                viewModel.deleteCategory(it)
                            }
                        }, "Are you sure you want to delete this category, big boi?")
                    }
                    if (openEditDialog.value) {
                        CategoryEntryDialog(
                            onConfirmClick = {
                                coroutineScope.launch {
                                    viewModel.editCategory()
                                }
                            },
                            onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                            viewModel = viewModel,
                            edit = true,
                            title = "Edit Category",
                            selectedCategory = it.toCategoryDetails()
                        )
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
    list: List<Payee>,
    coroutineScope: CoroutineScope,
    viewModel: EntityViewModel
) {
    LazyColumn {
        items(list) {
            ListItem(
                headlineContent = { Text(it.payeeName) },
                overlineContent = { Text(it.payeeId.toString()) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                    )
                },
                trailingContent = {
                    val openDeleteAlertDialog = remember { mutableStateOf(false) }
                    val openEditDialog = remember { mutableStateOf(false) }
                    val moreExpanded = remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            moreExpanded.value = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = modifier.size(36.dp, 36.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = moreExpanded.value,
                        onDismissRequest = { moreExpanded.value = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Edit") },
                            onClick = {
                                openEditDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Delete") },
                            onClick = {
                                openDeleteAlertDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                    }

                    if (openDeleteAlertDialog.value) {
                        DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                            coroutineScope.launch {
                                viewModel.deletePayee(it)
                            }
                        }, "Are you sure you want to delete this payee, big boi?")
                    }
                    if (openEditDialog.value) {
                        PayeeEntryDialog(
                            onConfirmClick = {
                                coroutineScope.launch {
                                    viewModel.editPayee()
                                }
                            },
                            onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                            viewModel = viewModel,
                            edit = true,
                            title = "Edit Payee",
                            selectedPayee = it.toPayeeDetails()
                        )
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
    list: List<CurrencyFormat>,
    coroutineScope: CoroutineScope,
    viewModel: EntityViewModel
) {
    LazyColumn {
        items(list) {
            ListItem(
                headlineContent = { Text(it.baseConvRate.toString()) },
                overlineContent = { Text( removeTrPrefix(it.currencyName)) },
                leadingContent = { Text(it.currency_symbol) },
                trailingContent = {
                    val openDeleteAlertDialog = remember { mutableStateOf(false) }
                    val openEditDialog = remember { mutableStateOf(false) }
                    val moreExpanded = remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            moreExpanded.value = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = modifier.size(36.dp, 36.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = moreExpanded.value,
                        onDismissRequest = { moreExpanded.value = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Edit") },
                            onClick = {
                                openEditDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Delete") },
                            onClick = {
                                openDeleteAlertDialog.value = true
                                moreExpanded.value = false
                            }
                        )
                    }

                    if (openDeleteAlertDialog.value) {
                        DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                            coroutineScope.launch {
                                viewModel.deleteCurrency(it)
                            }
                        }, "Are you sure you want to delete this currency, big boi?")
                    }
                    if (openEditDialog.value) {
                        CurrencyEntryDialog(
                            onConfirmClick = {
                                coroutineScope.launch {
                                    viewModel.editCurrency()
                                }
                            },
                            onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                            viewModel = viewModel,
                            edit = true,
                            title = "Edit Currency",
                            selectedCurrency = it.toCurrencyDetails()
                        )
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CategoryEntryDialog(
    modifier: Modifier = Modifier,
    title : String = "Add Category",
    selectedCategory : CategoryDetails = CategoryDetails(categName = ""),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel,
    edit : Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var categorySelected by remember { mutableStateOf(selectedCategory) }

    viewModel.updateCategoryState(
        viewModel.categoryUiState.categoryDetails.copy(
            categName = categorySelected.categName,
            categId = categorySelected.categId,
            active = categorySelected.active,
            parentId = categorySelected.parentId
        )
    )

    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = categorySelected.categName,
                    onValueChange = {
                        categorySelected.categName = it
                        viewModel.updateCategoryState(
                            categorySelected.copy(
                                categName = it
                            )
                        )
                    },
                    label = { Text("Category Name *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            viewModel.updateCategoryState(
                                viewModel.categoryUiState.categoryDetails.copy(
                                    active = "1",
                                    parentId = "-1"
                                )
                            )
                            onConfirmClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = viewModel.categoryUiState.isEntryValid
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun PayeeEntryDialog(
    modifier: Modifier = Modifier,
    title : String = "Add Payee",
    selectedPayee : PayeeDetails = PayeeDetails(payeeName = ""),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel,
    edit : Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var payeeSelected by remember { mutableStateOf(selectedPayee) }

    viewModel.updatePayeeState(
        viewModel.payeeUiState.payeeDetails.copy(
            payeeName = payeeSelected.payeeName,
            payeeId = payeeSelected.payeeId,
            categId = payeeSelected.categId,
            number = payeeSelected.number,
            website = payeeSelected.website,
            notes = payeeSelected.notes,
            active = payeeSelected.active
        )
    )
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(535.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.payeeName,
                    onValueChange = {
                        payeeSelected.payeeName = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                payeeName = it
                            )
                        )
                    },
                    label = { Text("Payee Name *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                Row(
                    modifier = Modifier.padding(0.dp, 8.dp),
                ) {
                    Checkbox(
                        checked = payeeSelected.active.toBoolean(),
                        onCheckedChange = {
                            payeeSelected.active = it.toString()
                            viewModel.updatePayeeState(
                                viewModel.payeeUiState.payeeDetails.copy(
                                    active = (it).toString()
                                )
                            )
                        },
                    )
                    Text(
                        text = "Hidden",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                // We're obviously not including last used category -_-
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.number,
                    onValueChange = {
                        payeeSelected.number = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                number = it
                            )
                        )
                    },
                    label = { Text("Reference Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.website,
                    onValueChange = {
                        payeeSelected.website = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                website = it
                            )
                        )
                    },
                    label = { Text("Website") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = payeeSelected.notes,
                    onValueChange = {
                        payeeSelected.notes = it
                        viewModel.updatePayeeState(
                            viewModel.payeeUiState.payeeDetails.copy(
                                notes = it
                            )
                        )
                    },
                    label = { Text("Notes") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirmClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = viewModel.payeeUiState.isEntryValid
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CurrencyEntryDialog(
    modifier: Modifier = Modifier,
    title : String = "Add Currency",
    selectedCurrency : CurrencyDetails = CurrencyDetails(),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel,
    edit : Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var currencySelected by remember { mutableStateOf(selectedCurrency) }

    viewModel.updateCurrencyState(
        viewModel.currencyUiState.currencyDetails.copy(
            currencyName = selectedCurrency.currencyName
        )
    )
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(225.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = currencySelected.currencyName,
                    onValueChange = {
                        currencySelected.currencyName = it
                        viewModel.updateCurrencyState(
                            viewModel.currencyUiState.currencyDetails.copy(
                                currencyName = it
                            )
                        )
                    },
                    label = { Text("Currency Name *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = viewModel.categoryUiState.categoryDetails.categName,
                    onValueChange = {
                        viewModel.updateCurrencyState(
                            viewModel.currencyUiState.currencyDetails.copy(
                                pfx_symbol = it
                            )
                        )
                    },
                    label = { Text("Currency Code *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                OutlinedTextField(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    value = viewModel.categoryUiState.categoryDetails.categName,
                    onValueChange = {
                        viewModel.updateCurrencyState(
                            viewModel.currencyUiState.currencyDetails.copy(
                                currency_symbol = it
                            )
                        )
                    },
                    label = { Text("Currency Symbol *") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    })
                )
                val radioOptions = listOf("Prefix", "Postfix")
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
                Column(Modifier.selectableGroup()) {
                    radioOptions.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
                // TODO: MAKE THE REST
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirmClick()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = viewModel.currencyUiState.isEntryValid
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}