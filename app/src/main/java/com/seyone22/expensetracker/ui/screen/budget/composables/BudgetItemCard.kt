package com.seyone22.expensetracker.ui.screen.budget.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.utils.TransactionType
import com.seyone22.expensetracker.utils.convertBudgetValue
import com.seyone22.expensetracker.utils.formatCurrency
import com.seyone22.expensetracker.utils.getValueWithType

@Composable
fun BudgetItemCard(
    modifier: Modifier,
    category: Category?,
    budgetItem: BudgetEntry?,
    targetPeriod: String?,
    expenseForCategory: Double,
    currencyFormat: CurrencyFormat,
    cardClickAction: () -> Unit,
) {
    val actualValueForPeriod = convertBudgetValue(budgetItem, targetPeriod)
    val actualValue = getValueWithType(actualValueForPeriod)
    val ratio = expenseForCategory.coerceAtLeast(1.0) / (actualValue?.first ?: 1.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { cardClickAction() },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(end = 32.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { ratio.toFloat() },
                    trackColor = MaterialTheme.colorScheme.inversePrimary,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    "${
                        (ratio * 100).coerceIn(0.0..1000.0).toInt()
                    }%"
                )
            }
            Column(
                modifier = Modifier.width(225.dp)
            ) {
                Row {
                    if (actualValue?.second == TransactionType.INCOME) {
                        Icon(
                            Icons.Filled.SouthWest, "", tint = Color.Green
                        )
                    } else {
                        Icon(
                            Icons.Filled.NorthEast, "", tint = Color.Red
                        )
                    }
                    Text(
                        text = "${category?.categName}",
                        fontWeight = if (category?.parentId == -1) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Text(
                    text = "${formatCurrency(expenseForCategory, currencyFormat)} out of ${
                        formatCurrency(actualValue?.first ?: 0.0, currencyFormat)
                    }"
                )
            }
            Box(
                modifier = Modifier, contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ChevronRight, "", modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}