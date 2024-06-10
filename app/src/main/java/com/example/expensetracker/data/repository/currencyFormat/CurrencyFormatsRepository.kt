package com.example.expensetracker.data.repository.currencyFormat

import com.example.expensetracker.data.model.CurrencyFormat
import kotlinx.coroutines.flow.Flow

interface CurrencyFormatsRepository {
    fun getAllCurrencyFormatsStream(): Flow<List<CurrencyFormat>>
    fun getCurrencyFormatStream(currencyId: Int): Flow<CurrencyFormat?>
    fun getCurrencyFormatsFromTypeStream(currencyId: Int): Flow<List<CurrencyFormat>>

    suspend fun insertCurrencyFormat(currency: CurrencyFormat)
    suspend fun deleteCurrencyFormat(currency: CurrencyFormat)
    suspend fun updateCurrencyFormat(currency: CurrencyFormat)
}