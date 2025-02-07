package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.screen.home.Totals

@Composable
fun NetWorth(
    totals: Totals, baseCurrencyInfo: CurrencyFormat, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        FormattedCurrency(
            value = totals.total,
            currency = baseCurrencyInfo,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Net Worth",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 0.dp, 0.dp, 24.dp),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 16px gap between cards
            modifier = Modifier.fillMaxWidth() // Make the row take up the full width
        ) {
            // First Card
            OutlinedCard(
                modifier = Modifier.weight(1f) // This makes the card take equal space
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {

                    Box(
                        modifier = Modifier
                            .size(36.dp) // Square container size
                            .background(
                                MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                            ), // Background with rounded corners
                        contentAlignment = Alignment.Center // Centers the icon inside the box
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NorthEast,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp) // Icon size
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Income",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )

                    FormattedCurrency(
                        value = totals.income,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // Second Card
            OutlinedCard(
                modifier = Modifier.weight(1f) // This makes the card take equal space
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp) // Square container size
                            .background(
                                MaterialTheme.colorScheme.error, shape = RoundedCornerShape(8.dp)
                            ), // Background with rounded corners
                        contentAlignment = Alignment.Center // Centers the icon inside the box
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SouthWest,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError, // Set the icon color to white
                            modifier = Modifier.size(36.dp) // Icon size
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Expenses",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    FormattedCurrency(
                        value = totals.expenses,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
