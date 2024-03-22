package com.example.expensetracker.ui.screen.onboarding

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.metadata.MetadataRepository
import com.example.expensetracker.data.prepopulate
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel to retrieve all items in the Room database.
 */
class OnboardingViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    var metadataUiState by mutableStateOf(MetadataUiState())
        private set

    val baseCurrencyId =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")
            .map { info ->
                info?.infoValue?.toInt() ?: 0
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(OnboardingViewModel.TIMEOUT_MILLIS),
                initialValue = 0
            )
    val isUsed =
        metadataRepository.getMetadataByNameStream("ISUSED")
            .map { info ->
                info?.infoValue?.toString() ?: "FALSE"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(OnboardingViewModel.TIMEOUT_MILLIS),
                initialValue = ""
            )
    val userName =
        metadataRepository.getMetadataByNameStream("USERNAME")
            .map { info ->
                info?.infoValue?.toString() ?: ""
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(OnboardingViewModel.TIMEOUT_MILLIS),
                initialValue = ""
            )

    val currencyList: StateFlow<CurrencyList> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
            .map { currencies ->
                CurrencyList(
                    currenciesList = currencies
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(OnboardingViewModel.TIMEOUT_MILLIS),
                initialValue = CurrencyList()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(metadataDetails: MetadataDetails) {
        metadataUiState = MetadataUiState(
            metadataDetails = metadataDetails,
            isEntryValid = validateInput(metadataDetails)
        )
    }

    suspend fun saveItems() {
        if (validateInput()) {
            Log.d("DEBUG", "saveItems: $metadataUiState")
            metadataRepository.insertMetadata(
                Metadata(
                    3, "CREATEDATE", LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                )
            )
            metadataRepository.insertMetadata(
                Metadata(
                    7,
                    "ISUSED",
                    "TRUE"
                )
            )
            //Causes Crash
            metadataRepository.insertMetadata(
                metadataUiState.metadataDetails.usernameMetadata
            )
            metadataRepository.insertMetadata(
                metadataUiState.metadataDetails.baseCurrencyMetadata
            )
        }
    }

    private fun validateInput(uiState: MetadataDetails = metadataUiState.metadataDetails): Boolean {
        return with(uiState) {
            uiState.usernameMetadata.infoValue.isNotBlank() && uiState.baseCurrencyMetadata.infoValue != "-1"
        }
    }

    suspend fun prepopulateDB(context : Context) {
        prepopulate(context, categoriesRepository, currencyFormatsRepository)
    }
}

data class CurrencyList(
    val currenciesList: List<CurrencyFormat> = listOf(),
)

data class MetadataUiState(
    val metadataDetails: MetadataDetails = MetadataDetails(),
    val isEntryValid: Boolean = false
)

//Data class for AccountDetails
data class MetadataDetails(
    val usernameMetadata: Metadata = (Metadata(6, "USERNAME", "")),
    val baseCurrencyMetadata: Metadata = (Metadata(5, "BASECURRENCYID", "")),
)