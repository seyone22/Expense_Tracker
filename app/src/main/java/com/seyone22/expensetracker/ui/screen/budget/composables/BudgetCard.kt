package com.seyone22.expensetracker.ui.screen.budget.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.ui.screen.budget.budgetDetail.BudgetDetailDestination

@Composable
fun BudgetCard(
    budgetYear: BudgetYear, isYearBudget: Boolean, navigateToScreen: (screen: String) -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { navigateToScreen("${BudgetDetailDestination.route}/${budgetYear.budgetYearId}") },
        headlineContent = {
            Row {
                if (!isYearBudget) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    fontWeight = if (isYearBudget) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }, text = budgetYear.budgetYearName
                )
            }
        },
        trailingContent = {
            Icon(
                Icons.Filled.ChevronRight, "", modifier = Modifier.size(24.dp)
            )
        }
    )
    if (false) {
        HorizontalDivider()
    }
}