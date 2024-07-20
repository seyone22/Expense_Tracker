package com.example.expensetracker.ui.screen.accounts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.screen.accounts.Totals

@Composable
fun NetWorth(
    totals: Totals,
    baseCurrencyInfo: CurrencyFormat
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp)
            .padding(0.dp, 24.dp, 0.dp, 0.dp)
    ) {
        FormattedCurrency(
            value = totals.total,
            currency = baseCurrencyInfo,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
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
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 24.dp),
        ) {
            Card(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    .width(165.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xff50b381),
                        modifier = Modifier.size(36.dp, 36.dp)
                    )
                    FormattedCurrency(
                        value = totals.income,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
            Card(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    .width(165.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowUpward,
                        contentDescription = null,
                        tint = Color(0xfff75e51),
                        modifier = Modifier.size(36.dp, 36.dp)
                    )
                    FormattedCurrency(
                        value = totals.expenses,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
        }
    }
}