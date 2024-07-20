package com.example.expensetracker.data.repository.currencyHistory

import com.example.expensetracker.data.model.CurrencyHistory
import kotlinx.coroutines.flow.Flow

interface CurrencyHistoryRepository {
    fun getAllCurrencyHistoryStream(): Flow<List<CurrencyHistory>>
    fun getCurrencyHistoryStream(currencyId: Int): Flow<CurrencyHistory?>
    fun getCurrencyHistoryFromTypeStream(currencyId: Int): Flow<List<CurrencyHistory>>

    suspend fun insertCurrencyHistory(currency: CurrencyHistory)
    suspend fun deleteCurrencyHistory(currency: CurrencyHistory)
    suspend fun updateCurrencyHistory(currency: CurrencyHistory)
}