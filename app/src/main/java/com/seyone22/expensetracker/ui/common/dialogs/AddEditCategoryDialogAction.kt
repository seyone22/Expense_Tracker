package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.ui.AppViewModelProvider


class AddEditCategoryDialogAction(
    private val onAdd: (Category) -> Unit,
    private val onEdit: (Category) -> Unit,
    initialEntry: Category? = null,
) : DialogAction {
    private val existingEntry = initialEntry

    override val title = if (initialEntry == null) "Add Category" else "Edit Category"
    override val message = null

    private var categorySelected by mutableStateOf(
        initialEntry ?: Category(
            categId = 0, parentId = -1, categName = "", active = 1
        )
    )
    private var categoryParent by mutableStateOf<Category?>(null)
    private var categoryExpanded by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable () -> Unit = {
        // Code block to get the current currency's detail.
        val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val categoriesList by sharedViewModel.categoriesFlow.collectAsState(initial = listOf())

        val focusManager = LocalFocusManager.current

        // Set categoryParent correctly when initialEntry exists
        LaunchedEffect(initialEntry) {
            if (initialEntry != null) {
                categoryParent = categoriesList.find { it.categId == initialEntry.parentId }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp, 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleLarge
            )

            // Parent Category Dropdown
            ExposedDropdownMenuBox(expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = categoryParent?.categName ?: "None",
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Parent Category") },
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    categoriesList.forEach { category ->
                        DropdownMenuItem(text = { Text(category.categName) }, onClick = {
                            categoryParent = category
                            categorySelected = categorySelected.copy(parentId = category.categId)
                            categoryExpanded = false
                        })
                    }
                }
            }

            // Category Name Input
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = categorySelected.categName,
                onValueChange = { newName ->
                    categorySelected = categorySelected.copy(categName = newName)
                },
                label = { Text("Category Name *") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(FocusDirection.Next)
                })
            )
        }
    }

    override fun onConfirm() {
        if (categorySelected.categName.isBlank()) return // Prevent empty categories
        if (existingEntry == null) {
            onAdd(categorySelected)
        } else {
            onEdit(categorySelected)
        }
    }

    override fun onCancel() {
        // No-op
    }
}