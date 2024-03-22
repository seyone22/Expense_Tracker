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

// TODO : Recurring Transactions
// TODO: Reports, Transaction Reports PRIORITY
// TODO: Budget setup, Budgets
// TODO: Stock Portfolio
// TODO: Assets
// TODO: Import/Export databases, transactions -> as format mmdb, csv, etc...
// TODO: Handle Attachments

// TODO : Multiple databases / switching databases

/* Settings stuff
    user name
    language
    date format
    base currency
    currency format
    currency history
    financial year start day
    financial year start month
    use original date when pasting transactions
    use original date when duplicating transactions

    view budgets as financial yars
    view budgets with transfer transactions
    view budget category report with summaries
    override yearly budget with munthly budget
    subtract monthly budgets from yearly budget in reporting
    budget offset days
    startday of month for repoirting
    ignore future transactions

    Defaults for new transaction dialog
    backup options
    deleted transactions retainment
    csv delimiter
*/

// NEW FEATURES
// TODO: automatic Interest handling for accounts