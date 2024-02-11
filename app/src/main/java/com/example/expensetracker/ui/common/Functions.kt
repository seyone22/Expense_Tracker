package com.example.expensetracker.ui.common

import android.icu.text.DecimalFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.model.CurrencyFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FormattedCurrency(
    modifier: Modifier = Modifier,
    value : Double,
    currency : CurrencyFormat,
    type: TransactionType = TransactionType.NEUTRAL
) {
    if (currency.pfx_symbol != "") {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "${currency.pfx_symbol}${DecimalFormat("#.##").format(value)}",
            color = if(type == TransactionType.DEBIT) { Color.Red } else  { MaterialTheme.colorScheme.onBackground }
        )
    } else {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "${DecimalFormat("#.##").format(value)}${currency.sfx_symbol}",
            color = if(type == TransactionType.DEBIT) { Color.Red } else  { MaterialTheme.colorScheme.onBackground }
        )
    }
}

fun removeTrPrefix(input: String): String {
    val prefix = "_tr_"

    return if (input.startsWith(prefix)) {
        input.removePrefix(prefix)
    } else {
        input
    }
}

fun getAbbreviatedMonthName(monthValue: Int, locale: Locale = Locale.getDefault()): String {
    val month = Month.of(monthValue)
    return month.getDisplayName(TextStyle.SHORT, locale)
}

enum class TransactionType {
    DEBIT,
    CREDIT,
    NEUTRAL
}


enum class EntryFields {
    STATUS,
    TYPE,
    ACCOUNT,
    PAYEE,
    CATEGORY
}