package com.seyone22.expensetracker.ui.common.dialogs

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.ui.common.inputs.DropdownSelector
import kotlin.math.absoluteValue

class AddEditBudgetEntryDialogAction(
    private val onAdd: (BudgetEntry) -> Unit = { },
    private val onEdit: (BudgetEntry) -> Unit = { },
    initialEntry: BudgetEntry? = null,
    categId: Int,
    budgetYearId: Int
) : DialogAction {
    private val existingEntry = initialEntry
    private val thisCategId = categId
    private val thisBudgetYearId = budgetYearId

    private var _selectedType by mutableStateOf("Expense")
    private var _selectedFrequency by mutableStateOf(initialEntry?.period ?: "Monthly")
    private var _amount by mutableStateOf((initialEntry?.amount?.absoluteValue)?.toString() ?: "")
    private var _notes by mutableStateOf(initialEntry?.notes ?: "")

    override val title = if (initialEntry == null) "Add Budget Entry" else "Edit Budget Entry"
    override val message = null

    override val content: @Composable () -> Unit = {
        Log.d("TAG", ": $existingEntry")
        Column(modifier = Modifier.padding(16.dp)) {

            // Type Picker (Expense / Income)
            TypePicker(
                selectedType = _selectedType,
                onTypeSelected = { _selectedType = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Frequency Picker
            FrequencyPicker(
                selectedFrequency = _selectedFrequency,
                onFrequencySelected = { _selectedFrequency = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amount Input Field
            OutlinedTextField(
                value = _amount,
                onValueChange = { _amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Notes Input Field
            OutlinedTextField(
                value = _notes,
                onValueChange = { _notes = it },
                label = { Text("Notes") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    override fun onConfirm() {
        var amountValue = 0.0
        if (_selectedType == "Expense") {
            amountValue = _amount.toDoubleOrNull()?.times(-1.0)
                ?: 0.0  // Convert to double, default to 0 if invalid
        } else if (_selectedType == "Income") {
            amountValue =
                _amount.toDoubleOrNull() ?: 0.0  // Convert to double, default to 0 if invalid
        }
        val entry = BudgetEntry(
            budgetEntryId = existingEntry?.budgetEntryId
                ?: 0, // Use existing ID for editing, default to 0 for new entries
            budgetYearId = existingEntry?.budgetYearId
                ?: thisBudgetYearId, // Adjust based on your actual data model
            categId = thisCategId, // Adjust based on your actual data model
            period = _selectedFrequency,
            amount = amountValue,
            notes = _notes
        )

        if (existingEntry == null) {
            onAdd(entry)
        } else {
            onEdit(entry)
        }
    }

    override fun onCancel() {
        // Handle cancel action if needed
    }
}

@Composable
fun TypePicker(
    selectedType: String, onTypeSelected: (String) -> Unit, modifier: Modifier = Modifier
) {
    val types = listOf("Expense", "Income")

    DropdownSelector(
        items = types,
        selectedItem = selectedType,
        onItemSelected = { sel -> onTypeSelected(sel) },
        label = "Select Type",
        modifier = modifier
    )
}


@Composable
fun FrequencyPicker(
    selectedFrequency: String, onFrequencySelected: (String) -> Unit, modifier: Modifier = Modifier
) {
    val frequencies = listOf(
        "None",
        "Daily",
        "Weekly",
        "Every 2 Weeks",
        "Monthly",
        "Every 2 Months",
        "Quarterly",
        "Half-yearly", // Fixed typo
        "Yearly"
    )

    DropdownSelector(
        items = frequencies,
        selectedItem = selectedFrequency,
        onItemSelected = { selected -> onFrequencySelected(selected) },
        label = "Select Frequency",
        modifier = modifier
    )
}