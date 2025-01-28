package com.example.expensetracker.ui.common.dialogs

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategoryDetails
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.TransactionUiState
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun CategoryEntryDialog(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    transactionViewModel: TransactionEntryViewModel,
    title: String = "Add Category",
    selectedCategory: CategoryDetails = CategoryDetails(categName = ""),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var categorySelected by remember { mutableStateOf(selectedCategory) }
    var categoryParent by remember { mutableStateOf(Category(categId = -1)) }

    var categoryExpanded by remember { mutableStateOf(false) }

    val transactionUiState: TransactionUiState by transactionViewModel.transactionUiState1.collectAsState()
    Log.d("TAG", "CategoryEntryDialog: $transactionUiState")

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
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                // Transaction Categories
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = {
                    categoryExpanded = !categoryExpanded

                }) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(0.dp, 8.dp)
                            .clickable(enabled = true) {
                                categoryExpanded = true

                            }
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                        value = removeTrPrefix(categoryParent.categName),
                        readOnly = true,
                        onValueChange = {
                            categorySelected =
                                categorySelected.copy(parentId = categoryParent.categId.toString())
                            viewModel.updateCategoryState(
                                categorySelected.copy(
                                    parentId = categoryParent.categId.toString()
                                )
                            )
                        },
                        label = { Text("Parent Category") },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.moveFocus(
                                FocusDirection.Next
                            )
                        }),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        transactionUiState.categoriesList.forEach { category ->
                            DropdownMenuItem(text = {
                                Row {
                                    if (category.parentId != -1) {
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                    Text(removeTrPrefix(category.categName))
                                }
                            }, onClick = {
                                categoryParent = category
                                categoryExpanded = false
                            })
                        }
                    }
                }

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
                                    parentId = categoryParent.categId.toString()
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
    title: String = "Add Payee",
    selectedPayee: PayeeDetails = PayeeDetails(payeeName = ""),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val payeeSelected by remember { mutableStateOf(selectedPayee) }

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
                        .padding(0.dp, 8.dp)
                        .fillMaxWidth(),
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
    title: String = "Add Currency",
    selectedCurrency: CurrencyDetails = CurrencyDetails(),
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val currencySelected by remember { mutableStateOf(selectedCurrency) }

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
                .height(500.dp)
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