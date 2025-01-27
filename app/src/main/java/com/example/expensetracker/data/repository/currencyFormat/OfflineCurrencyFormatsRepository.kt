package com.example.expensetracker.data.repository.currencyFormat

import com.example.expensetracker.data.model.CurrencyFormat
import kotlinx.coroutines.flow.Flow

class OfflineCurrencyFormatsRepository(private val currencyFormatDao: CurrencyFormatDao) :
    CurrencyFormatsRepository {
    override fun getAllCurrencyFormatsStream(): Flow<List<CurrencyFormat>> =
        currencyFormatDao.getAllCurrencyFormats()

    override fun getCurrencyFormatStream(currencyId: Int): Flow<CurrencyFormat?> =
        currencyFormatDao.getCurrencyFormat(currencyId)

    override fun getCurrencyFormatsFromTypeStream(currencyId: Int): Flow<List<CurrencyFormat>> =
        currencyFormatDao.getAllCurrencyFormatsByCurrencyFormat(currencyId)

    override fun getActiveCurrencies(): Flow<List<Int>> = currencyFormatDao.getActiveCurrencies()

    override suspend fun insertCurrencyFormat(currency: CurrencyFormat) =
        currencyFormatDao.insert(currency)

    override suspend fun deleteCurrencyFormat(currency: CurrencyFormat) =
        currencyFormatDao.delete(currency)

    override suspend fun updateCurrencyFormat(currency: CurrencyFormat) =
        currencyFormatDao.update(currency)
}