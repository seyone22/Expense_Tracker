package com.example.expensetracker

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.Metadata
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.repository.metadata.MetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SharedViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : ViewModel() {
    val baseCurrencyIdFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")

    // Flow to retrieve and convert "ISUSED" metadata to a boolean
    val isUsedFlow: Flow<Boolean> = metadataRepository.getMetadataByNameStream("ISUSED")
        .map { metadata -> metadata?.infoValue == "TRUE" }

    // Stream for currency format using the base currency ID
    val baseCurrencyFlow: Flow<CurrencyFormat?> =
        baseCurrencyIdFlow.combine(currencyFormatsRepository.getAllCurrencyFormatsStream()) { baseCurrencyId, allCurrencyFormats ->
            // Use the baseCurrencyId to get the corresponding CurrencyFormat
            allCurrencyFormats.firstOrNull { it.currencyId == baseCurrencyId?.infoValue?.toInt() }
        }
    //serve it globally
}



