package com.example.expensetracker.ui.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.example.expensetracker.data.metadata.MetadataRepository
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Metadata
import com.example.expensetracker.ui.screen.onboarding.CurrencyList
import com.example.expensetracker.ui.theme.DarkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

/**
 * ViewModel to retrieve all items in the Room database.
 */
class SettingsViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository

) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Flow for username
    private val usernameFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("USERNAME")

    // Flow for baseCurrency
    private val baseCurrencyIdFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")

    private val requireUnlockFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("REQUIREUNLOCK")

    private val secureScreenFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("SECURESCREEN")

    val securityObject: Flow<SecurityObject> =
        combine(requireUnlockFlow, secureScreenFlow) {r, s ->
            SecurityObject(r?.infoValue.toBoolean(), s?.infoValue.toBoolean())
        }

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
        val x = metadataRepository.getMetadataByNameStream("THEME").firstOrNull() ?: return DarkTheme()
        when(x.infoValue) {
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
        when(theme) {
            0 -> {
                metadataRepository.insertMetadata(com.example.expensetracker.model.Metadata(99,"THEME","LIGHT"))
            }
            1 -> {
                metadataRepository.insertMetadata(com.example.expensetracker.model.Metadata(99,"THEME","DARK"))
            }
            2 -> {
                metadataRepository.insertMetadata(com.example.expensetracker.model.Metadata(99,"THEME","SYSTEM"))
            }
            3 -> {
                metadataRepository.insertMetadata(com.example.expensetracker.model.Metadata(99,"THEME","MIDNIGHT"))
            }
        }
    }

    suspend fun changeUsername(newName: String) {
        metadataRepository.updateMetadata(
            Metadata(6, "USERNAME", newName)
        )
    }

    suspend fun getRequireUnlock() : Boolean {
        return metadataRepository.getMetadataByNameStream("REQUIREUNLOCK").firstOrNull()?.infoValue.toString().toBoolean()
    }

    suspend fun setRequireUnlock(value: Boolean) {
        metadataRepository.insertMetadata( com.example.expensetracker.model.Metadata(100, "REQUIREUNLOCK", value.toString()) )
    }

    suspend fun getSecureScreen() : Boolean {
        return metadataRepository.getMetadataByNameStream("SECURESCREEN").firstOrNull()?.infoValue.toString().toBoolean()
    }

    suspend fun setSecureScreen(value: Boolean) {
        metadataRepository.insertMetadata( com.example.expensetracker.model.Metadata(101, "SECURESCREEN", value.toString()) )
    }

    suspend fun changeCurrency(newCurrency: Int) {
        metadataRepository.updateMetadata(
            Metadata(5, "BASECURRENCYID", newCurrency.toString())
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMonthlyRates(baseCurrencyId: Int) {
        viewModelScope.launch {
            val onlineData = withContext(Dispatchers.IO) {
                InfoEuroApi.retrofitService.getMonthlyRates()
            }

            val baseCurrency = getBaseCurrencyInfo(baseCurrencyId)

            val currencyList = currencyFormatsRepository.getAllCurrencyFormatsStream().first()

            flow {
                val onlineDataMap = onlineData.associateBy { it.isoA3Code }
                val baseCurrExchangeRate =
                    (onlineData.find { it.isoA3Code == baseCurrency.currency_symbol })!!.value

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
                        emit(currency) // Emit the original currency format if data is not found
                    }
                }
            }.collect {
                currencyFormatsRepository.updateCurrencyFormat(it)
                Log.d("TAG", it.toString())
            }

        }

    }
}

data class SecurityObject(
    var requireUnlock: Boolean = false,
    var secureScreen: Boolean = false
)