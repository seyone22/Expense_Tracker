package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.ui.common.inputs.DropdownSelector
import java.time.LocalDate

class AddEditBudgetYearDialogAction(
    private val onAdd: (String, Int?, BudgetYear) -> Unit,  // Adjusted to take year, month, and base budget
    initialYear: LocalDate = LocalDate.now(),
    initialMonth: LocalDate = LocalDate.now(),
    private val availableBudgets: List<BudgetYear> = emptyList()
) : DialogAction {
    private var _budgetYear by mutableStateOf(initialYear)
    private var _budgetMonth by mutableStateOf(initialMonth)
    private var _selectedBaseBudget by mutableStateOf(
        availableBudgets.firstOrNull() ?: BudgetYear(-1, "None")
    )
    private var _isBudgetForMonthChecked by mutableStateOf(false)  // Track if the checkbox is checked

    override val title = "Add Budget Year"
    override val message = null

    override val content: @Composable () -> Unit = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Checkbox for "Budget for Month"
            Row(
                modifier = Modifier.width(270.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget Type")
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = !_isBudgetForMonthChecked,
                        onClick = { _isBudgetForMonthChecked = false },
                        label = { Text("Year") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    )
                    SegmentedButton(
                        selected = _isBudgetForMonthChecked,
                        onClick = { _isBudgetForMonthChecked = true },
                        label = { Text("Month") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    )
                }
            }

            // Date Picker (Year Selection Only)
            YearPicker(selectedDate = _budgetYear, onDateSelected = { _budgetYear = it })

            // If "Budget for Month" is checked, show the Month Picker
            if (_isBudgetForMonthChecked) {
                // Animated MonthPicker based on checkbox state
                AnimatedVisibility(
                    visible = _isBudgetForMonthChecked,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300))
                ) {
                    MonthPicker(
                        selectedDate = _budgetMonth,
                        onDateSelected = { _budgetMonth = it },
                        modifier = Modifier.animateContentSize() // Smooth transition for content size
                    )
                }
            }

            // Dropdown for Base Budget Selection
            BaseBudgetDropdown(availableBudgets = availableBudgets,
                selectedBaseBudget = _selectedBaseBudget,
                onBaseBudgetSelected = {
                    if (it != null) {
                        _selectedBaseBudget = it
                    }
                })
        }
    }

    override fun onConfirm() {
        // Extract the year and month values
        val month =
            if (_isBudgetForMonthChecked) _budgetMonth.monthValue else null  // Set month only if checked
        onAdd(
            _budgetYear.year.toString(), month, _selectedBaseBudget
        )  // Pass year, month (nullable), and base budget
    }

    override fun onCancel() {
        // Handle cancel action if needed
    }
}

@Composable
fun YearPicker(
    selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit, modifier: Modifier = Modifier
) {
    val currentYear = LocalDate.now().year
    val years = (currentYear..currentYear + 50).toList()

    DropdownSelector(
        items = years,
        selectedItem = selectedDate.year,
        onItemSelected = { selectedYear -> onDateSelected(LocalDate.of(selectedYear, 1, 1)) },
        label = "Select Year",
        modifier = modifier
    )
}

@Composable
fun MonthPicker(
    selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit, modifier: Modifier = Modifier
) {
    val currentYear = LocalDate.now().year
    val months = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    DropdownSelector(
        items = months,
        selectedItem = months[selectedDate.monthValue - 1], // Adjust to 0-based index
        onItemSelected = { selectedMonth ->
            val selectedMonthIndex = months.indexOf(selectedMonth) + 1 // Get the 1-based month
            onDateSelected(LocalDate.of(currentYear, selectedMonthIndex, 1))
        },
        label = "Select Month",
        modifier = modifier
    )
}

@Composable
fun BaseBudgetDropdown(
    availableBudgets: List<BudgetYear>,
    selectedBaseBudget: BudgetYear,
    onBaseBudgetSelected: (BudgetYear?) -> Unit, // Nullable BudgetYear
    modifier: Modifier = Modifier
) {
    DropdownSelector(items = listOf(BudgetYear(-1, "None")) + availableBudgets,
        selectedItem = BudgetYear(-1, "None"),
        onItemSelected = { onBaseBudgetSelected(it) },
        label = "Base Budget on",
        modifier = modifier,
        itemToString = { it.budgetYearName })
}





