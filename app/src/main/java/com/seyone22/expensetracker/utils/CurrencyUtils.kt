package com.seyone22.expensetracker.utils

import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.CurrencyHistory
import com.seyone22.expensetracker.data.model.InfoEuroCurrencyListResponse
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TransactionType {
    INCOME,
    EXPENSE
}

fun getValueWithType(value: Double?): Pair<Double, TransactionType>? {
    if (value == null) return null
    return if (value < 0) {
        Pair(value * -1, TransactionType.EXPENSE)
    } else {
        Pair(value, TransactionType.INCOME)
    }
}

fun formatCurrency(value: Double, currency: CurrencyFormat): String {
    // Format the number with comma separators
    val formattedValue = DecimalFormat("#,###.00").format(value)

    // Return formatted string with the appropriate currency symbol
    return if (currency.pfx_symbol.isNotEmpty()) {
        "${currency.pfx_symbol} $formattedValue"
    } else {
        "$formattedValue${currency.sfx_symbol}"
    }
}


// Utility function to get monthly rates and update currency formats/history
suspend fun updateCurrencyFormatsAndHistory(
    onlineData: List<InfoEuroCurrencyListResponse>,
    baseCurrency: CurrencyFormat,
    currencyFormatsRepository: CurrencyFormatsRepository,
    currencyHistoryRepository: CurrencyHistoryRepository
) {
    val currencyList = currencyFormatsRepository.getAllCurrencyFormatsStream().first()
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Update currency formats
    flow {
        val onlineDataMap = onlineData.associateBy { it.isoA3Code }
        val baseCurrExchangeRate =
            onlineData.find { it.isoA3Code == baseCurrency.currency_symbol }!!.value

        for (currency in currencyList) {
            val datum = onlineDataMap[currency.currency_symbol]
            if (datum != null) {
                val updatedCurr = CurrencyFormat(
                    currency.currencyId,
                    currency.currencyName,
                    currency.pfx_symbol,
                    currency.sfx_symbol,
                    currency.decimal_point,
                    currency.group_seperator,
                    currency.unit_name,
                    currency.cent_name,
                    currency.scale,
                    ((1 / datum.value) * baseCurrExchangeRate),
                    currency.currency_symbol,
                    currency.currency_type
                )
                emit(updatedCurr)
            } else {
                emit(currency) // Emit original if no data found
            }
        }
    }.collect {
        currencyFormatsRepository.updateCurrencyFormat(it)
    }

    // Insert currency history
    flow {
        val onlineDataMap = onlineData.associateBy { it.isoA3Code }
        val baseCurrExchangeRate =
            onlineData.find { it.isoA3Code == baseCurrency.currency_symbol }!!.value

        for (currency in currencyList) {
            val datum = onlineDataMap[currency.currency_symbol]
            if (datum != null) {
                val historyEntry = CurrencyHistory(
                    currencyId = currency.currencyId,
                    currDate = LocalDate.now().format(dateFormatter),
                    currValue = ((1 / datum.value) * baseCurrExchangeRate),
                    currUpdType = 1
                )
                emit(historyEntry)
            }
        }
    }.collect {
        currencyHistoryRepository.insertCurrencyHistory(it)
    }
}