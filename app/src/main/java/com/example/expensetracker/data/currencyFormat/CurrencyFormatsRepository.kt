package com.example.expensetracker.data.currencyFormat

import com.example.expensetracker.model.CurrencyFormat
import kotlinx.coroutines.flow.Flow

interface CurrencyFormatsRepository {
    fun getAllCurrencyFormatsStream(): Flow<List<CurrencyFormat>>
    fun getCurrencyFormatStream(currencyId: Int): Flow<CurrencyFormat?>
    fun getCurrencyFormatsFromTypeStream(currencyId: Int): Flow<List<CurrencyFormat>>

    suspend fun insertCurrencyFormat(currency: CurrencyFormat)
    suspend fun deleteCurrencyFormat(currency: CurrencyFormat)
    suspend fun updateCurrencyFormat(currency: CurrencyFormat)
}