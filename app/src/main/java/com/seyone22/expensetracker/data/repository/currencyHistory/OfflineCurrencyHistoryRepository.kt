package com.seyone22.expensetracker.data.repository.currencyHistory

import com.seyone22.expensetracker.data.model.CurrencyHistory
import kotlinx.coroutines.flow.Flow

class OfflineCurrencyHistoryRepository(private val currencyHistoryDao: CurrencyHistoryDao) :
    CurrencyHistoryRepository {
    override fun getAllCurrencyHistoryStream(): Flow<List<CurrencyHistory>> =
        currencyHistoryDao.getAllCurrencyHistory()

    override fun getCurrencyHistoryStream(currencyId: Int): Flow<CurrencyHistory?> =
        currencyHistoryDao.getCurrencyHistory(currencyId)

    override fun getCurrencyHistoryFromTypeStream(currencyId: Int): Flow<List<CurrencyHistory>> =
        currencyHistoryDao.getAllCurrencyHistoryByCurrencyHistory(currencyId)

    override suspend fun insertCurrencyHistory(currency: CurrencyHistory) =
        currencyHistoryDao.insert(currency)

    override suspend fun deleteCurrencyHistory(currency: CurrencyHistory) =
        currencyHistoryDao.delete(currency)

    override suspend fun updateCurrencyHistory(currency: CurrencyHistory) =
        currencyHistoryDao.update(currency)
}