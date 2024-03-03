package com.example.expensetracker.ui.common

import android.icu.text.DecimalFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.model.CurrencyFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


const val TAG = "TESTING"
@Composable
fun FormattedCurrency(
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    value: Double,
    currency: CurrencyFormat,
    type: TransactionType = TransactionType.NEUTRAL
) {
    val formattedValue = DecimalFormat("#,###.##").format(value) // Add comma separators
    val textColor = if (type == TransactionType.DEBIT) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val displayText = if (currency.pfx_symbol.isNotEmpty()) {
        "${currency.pfx_symbol}$formattedValue"
    } else {
        "$formattedValue${currency.sfx_symbol}"
    }

    Text(
        style = style,
        text = displayText,
        color = textColor,
        modifier = modifier
    )
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
    CATEGORY,
    TRANSACTION
}