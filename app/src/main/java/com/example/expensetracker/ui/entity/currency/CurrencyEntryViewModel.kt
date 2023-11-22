package com.example.expensetracker.ui.entity.currency

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.model.CurrencyFormat

class CurrencyEntryViewModel(private val currencyFormatsRepository: CurrencyFormatsRepository) : ViewModel() {
    var currencyUiState by mutableStateOf(CurrencyUiState())
        private set

    fun updateUiState(currencyDetails: CurrencyDetails) {
        currencyUiState =
            CurrencyUiState(currencyDetails = currencyDetails, isEntryValid = validateInput(currencyDetails))
    }

    suspend fun saveCurrency() {
        Log.d("DEBUG", "saveCurrency: Called!")
        if(validateInput()) {
            Log.d("DEBUG", "saveCurrency: Input Valid!")
            currencyFormatsRepository.insertCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }

    private fun validateInput(uiState: CurrencyDetails = currencyUiState.currencyDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.currencyName)
        return with(uiState) {
            currencyName.isNotBlank()
        }
    }
}

//Data class for CurrencyUiState
data class CurrencyUiState(
    val currencyDetails: CurrencyDetails = CurrencyDetails(),
    val isEntryValid: Boolean = false
)
//Data class for CurrencyDetails
data class CurrencyDetails(
    val currencyId : Int = 0,
    val currencyName : String = "",
    val pfx_symbol : String = "",
    val sfx_symbol : String = "",
    val decimal_point : String = "",
    val group_seperator : String = "",
    val unit_name : String = "",
    val cent_name : String = "",
    val scale : String = "",
    val baseConvRate : String = "",
    val currency_symbol : String = "",
    val currency_type : String = ""
)


// Extension functions to convert between [Currency], [CurrencyUiState], and [CurrencyDetails]
fun CurrencyDetails.toCurrency(): CurrencyFormat = CurrencyFormat(
    currencyId = currencyId,
    currencyName = currencyName,
    pfx_symbol = pfx_symbol,
    sfx_symbol = sfx_symbol,
    decimal_point = decimal_point,
    group_seperator = group_seperator,
    unit_name = unit_name,
    cent_name = cent_name,
    scale = scale.toInt(),
    baseConvRate = baseConvRate.toDouble(),
    currency_symbol = currency_symbol,
    currency_type =currency_type
)

fun CurrencyFormat.toCurrencyUiState(isEntryValid: Boolean = false): CurrencyUiState = CurrencyUiState(
    currencyDetails = this.toCurrencyDetails(),
    isEntryValid = isEntryValid
)

fun CurrencyFormat.toCurrencyDetails(): CurrencyDetails = CurrencyDetails(
    currencyId = currencyId,
    currencyName = currencyName,
    pfx_symbol = pfx_symbol,
    sfx_symbol = sfx_symbol,
    decimal_point = decimal_point,
    group_seperator = group_seperator,
    unit_name = unit_name,
    cent_name = cent_name,
    scale = scale.toString(),
    baseConvRate = baseConvRate.toString(),
    currency_symbol = currency_symbol,
    currency_type =currency_type
)
