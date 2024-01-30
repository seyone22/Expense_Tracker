package com.example.expensetracker.ui.common

import android.icu.text.DecimalFormat
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.model.CurrencyFormat

@Composable
fun FormattedCurrency(
    modifier: Modifier = Modifier,
    value : Double,
    currency : CurrencyFormat
) {
    if (currency.pfx_symbol != "") {
        Text(text = "${currency.pfx_symbol}${DecimalFormat("#.##").format(value)}")
    } else {
        Text(text = "${DecimalFormat("#.##").format(value)}${currency.sfx_symbol}")
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
