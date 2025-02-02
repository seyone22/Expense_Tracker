package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.time.LocalDate

class AddEditBudgetYearDialogAction(
    private val onAdd: (String, LocalDate, String) -> Unit,
    private val onEdit: (String, LocalDate, String) -> Unit,
    private val isEditing: Boolean = false,
    initialValue: String = "",
    initialDate: LocalDate = LocalDate.now(),
    private val availableBudgets: List<String> = listOf()
) : DialogAction {
    private var _budgetYear = initialValue
    private var _selectedDate = initialDate
    private var _selectedBaseBudget: String = availableBudgets.firstOrNull() ?: ""


    override val title: String
        get() = if (isEditing) "Edit Budget Year" else "Add Budget Year"

    override val message: String
        get() = if (isEditing) "Modify the budget year name." else "Please enter the new budget year."

    override val content: @Composable () -> Unit = {
        Column {
            // Date Picker (Year Selection Only)
            YearPicker(selectedDate = _selectedDate, onDateSelected = { _selectedDate = it })

            // Dropdown for Base Budget Selection
            BaseBudgetDropdown(availableBudgets = availableBudgets,
                selectedBaseBudget = _selectedBaseBudget,
                onBaseBudgetSelected = { _selectedBaseBudget = it })
        }
    }

    override fun onConfirm() {
        if (isEditing) {
            onEdit(_budgetYear, _selectedDate, _selectedBaseBudget)
        } else {
            onAdd(_budgetYear, _selectedDate, _selectedBaseBudget)
        }
    }

    override fun onCancel() {
        // Handle cancel action if needed
    }
}


@Composable
fun YearPicker(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val year = selectedDate.year.toString()

    OutlinedTextField(
        value = year,
        onValueChange = { newYear ->
            val updatedDate = LocalDate.of(newYear.toIntOrNull() ?: 2025, 1, 1)
            onDateSelected(updatedDate)
        },
        label = { Text("Select Year") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBudgetDropdown(
    availableBudgets: List<String>,
    selectedBaseBudget: String,
    onBaseBudgetSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedBaseBudget,
            onValueChange = { },
            label = { Text("Base Budget") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .clickable { expanded = true } // Allow dropdown to open when clicked
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Add the "none" option
            DropdownMenuItem(
                onClick = {
                    onBaseBudgetSelected("none")
                    expanded = false
                },
                text = { Text(text = "none") }
            )
            // Populate available budgets
            availableBudgets.forEach { budget ->
                DropdownMenuItem(
                    onClick = {
                        onBaseBudgetSelected(budget)
                        expanded = false
                    },
                    text = { Text(text = budget) }
                )
            }
        }
    }
}

