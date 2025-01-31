package com.seyone22.expensetracker.ui.screen.entities.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.CurrencyHistory
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import kotlinx.coroutines.CoroutineScope

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrenciesList(
    modifier: Modifier = Modifier,
    list: Pair<List<CurrencyFormat>, List<CurrencyHistory>?>,
    coroutineScope: CoroutineScope,
    longClicked: (CurrencyFormat) -> Unit,
    onClicked: (String) -> Unit,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    LazyColumn {
        items(list.first, key = { it.currencyId }) {
            val x = list.second?.find { historyEntry -> historyEntry.currencyId == it.currencyId }
            ListItem(
                headlineContent = {
                    Row {
                        FormattedCurrency(
                            value = it.baseConvRate,
                            currency = CurrencyFormat(),
                        )
                        if (x != null) {
                            val difference = x.currValue.minus(it.baseConvRate)
                            if (x.currValue > it.baseConvRate) {
                                // Red down triangle and difference in red
                                Icon(
                                    imageVector = Icons.Filled.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = String.format("%.2f", difference),
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            } else if (x.currValue < it.baseConvRate) {
                                // Green up triangle and difference in green
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.Green,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = String.format("%.2f", -difference),
                                    color = Color.Green,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            // If the values are the same (to 2 decimal places), nothing is shown
                        }
                    }
                },
                overlineContent = { Text(removeTrPrefix(it.currencyName)) },
                leadingContent = {
                    Text(
                        text = it.currency_symbol,
                        modifier = Modifier.requiredWidth(48.dp)
                    )
                },
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onClicked(it.currency_symbol)
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(it)
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()
        }
    }
}