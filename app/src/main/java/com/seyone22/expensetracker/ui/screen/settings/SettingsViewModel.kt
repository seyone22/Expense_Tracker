package com.seyone22.expensetracker.ui.screen.settings

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.CurrencyHistory
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.ui.screen.onboarding.CurrencyList
import com.seyone22.expensetracker.ui.theme.DarkTheme
import com.seyone22.expensetracker.utils.SnackbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel to retrieve all items in the Room database.
 */
class SettingsViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository

) : BaseViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Flow for username
    private val usernameFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("USERNAME")

    // Flow for baseCurrency
    private val baseCurrencyIdFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")

    // Combine the flows and calculate the totals
    val metadataList: Flow<List<Metadata?>> =
        combine(usernameFlow, baseCurrencyIdFlow) { username, basecurrencyid ->
            listOf(username, basecurrencyid)
        }
    val currencyList: StateFlow<CurrencyList> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
            .map { currencies ->
                CurrencyList(
                    currenciesList = currencies
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrencyList()
            )

    suspend fun getBaseCurrencyInfo(baseCurrencyId: Int): CurrencyFormat {
        return currencyFormatsRepository.getCurrencyFormatStream(baseCurrencyId)
            .firstOrNull() ?: CurrencyFormat()
    }

    suspend fun getCurrentTheme(): DarkTheme {
        val x =
            metadataRepository.getMetadataByNameStream("THEME").firstOrNull() ?: return DarkTheme()
        when (x.infoValue) {
            "LIGHT" -> {
                return DarkTheme(false, false)
            }

            "DARK" -> {
                return DarkTheme(true, false)
            }

            "MIDNIGHHT" -> {
                return DarkTheme(true, true)
            }
        }
        return DarkTheme()
    }

    suspend fun setTheme(theme: Int) {
        when (theme) {
            0 -> {
                metadataRepository.insertMetadata(Metadata(99, "THEME", "LIGHT"))
            }

            1 -> {
                metadataRepository.insertMetadata(Metadata(99, "THEME", "DARK"))
            }

            2 -> {
                metadataRepository.insertMetadata(Metadata(99, "THEME", "SYSTEM"))
            }

            3 -> {
                metadataRepository.insertMetadata(Metadata(99, "THEME", "MIDNIGHT"))
            }
        }
    }

    suspend fun changeUsername(newName: String) {
        metadataRepository.updateMetadata(
            Metadata(6, "USERNAME", newName)
        )
    }

    suspend fun changeCurrency(newCurrency: Int) {
        metadataRepository.updateMetadata(
            Metadata(5, "BASECURRENCYID", newCurrency.toString())
        )
    }

    fun getMonthlyRates(baseCurrencyId: Int) {
        viewModelScope.launch {
            try {
                val onlineData = withContext(Dispatchers.IO) {
                    InfoEuroApi.retrofitService.getMonthlyRates()
                }

                val baseCurrency = getBaseCurrencyInfo(baseCurrencyId)
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
                    Log.d("TAG", "Updated Currency: $it")
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
                    Log.d("TAG", "Inserted History: $it")
                }

                // Show success Snackbar **after** both operations are completed
                SnackbarManager.showMessage("Currency formats updated successfully!")

            } catch (e: Exception) {
                SnackbarManager.showMessage("Failed to update currency formats: ${e.message}")
                Log.e("TAG", "Error updating currency formats", e)
            }
        }
    }
}