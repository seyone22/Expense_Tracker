package com.seyone22.expensetracker.managers

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import com.seyone22.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.utils.updateCurrencyFormatsAndHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class CurrencyManager(
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository
) {
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    suspend fun getMonthlyRates(baseCurrencyFlow: Flow<CurrencyFormat?>) {
        _isUpdating.value = true
        SnackbarManager.showMessage("Updating Currency Formats...", SnackbarDuration.Long)
        try {
            val onlineData = withContext(Dispatchers.IO) {
                InfoEuroApi.retrofitService.getMonthlyRates()
            }

            val baseCurrency = baseCurrencyFlow.first()
            if (baseCurrency !== null) {
                updateCurrencyFormatsAndHistory(
                    onlineData = onlineData,
                    baseCurrency = baseCurrency,
                    currencyFormatsRepository = currencyFormatsRepository,
                    currencyHistoryRepository = currencyHistoryRepository
                )
            }

            SnackbarManager.showMessage("Currency formats updated successfully!")
        } catch (e: Exception) {
            SnackbarManager.showMessage("Failed to update currency formats: ${e.message}")
            Log.e("TAG", "Error updating currency formats", e)
        } finally {
            _isUpdating.value = false
        }
    }

    suspend fun getCurrencyById(id: Int): CurrencyFormat? {
        return currencyFormatsRepository.getCurrencyFormatStream(id).firstOrNull()
    }
}