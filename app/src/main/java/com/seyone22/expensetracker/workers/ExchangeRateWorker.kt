package com.seyone22.expensetracker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seyone22.expensetracker.ExpenseApplication
import com.seyone22.expensetracker.managers.CurrencyManager
import kotlinx.coroutines.flow.combine

class ExchangeRateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("ExchangeRateWorker", "Starting background currency rates update...")
        return try {
            val container = (applicationContext as ExpenseApplication).container
            val currencyFormatsRepository = container.currenciesRepository
            val currencyHistoryRepository = container.currencyHistoryRepository
            val metadataRepository = container.metadataRepository

            val currencyManager = CurrencyManager(currencyFormatsRepository, currencyHistoryRepository)

            // Construct baseCurrencyFlow matching SharedViewModel logic
            val baseCurrencyFlow = metadataRepository.getMetadataByNameStream("BASECURRENCYID")
                .combine(currencyFormatsRepository.getAllCurrencyFormatsStream()) { baseCurrencyId, allCurrencyFormats ->
                    allCurrencyFormats.firstOrNull { it.currencyId == baseCurrencyId?.infoValue?.toIntOrNull() }
                }

            currencyManager.getMonthlyRates(baseCurrencyFlow)

            Result.success()
        } catch (e: Exception) {
            Log.e("ExchangeRateWorker", "Error updating monthly exchange rates in background", e)
            Result.retry()
        }
    }
}
