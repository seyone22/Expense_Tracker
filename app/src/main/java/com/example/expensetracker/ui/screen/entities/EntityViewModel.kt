package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryDetails
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryUiState
import com.example.expensetracker.ui.screen.operations.entity.category.toCategory
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyUiState
import com.example.expensetracker.ui.screen.operations.entity.currency.toCurrency
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeUiState
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayee
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class EntityViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [EntitiesRepository] and mapped to
     * [EntityUiState]
     */

    val entitiesUiState: StateFlow<EntitiesUiState> =
        categoriesRepository.getAllCategoriesStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { categories ->
                EntitiesUiState(
                    categoriesList = categories
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EntitiesUiState()
            )

    val entitiesUiState2: StateFlow<EntitiesUiState> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { currencies ->
                EntitiesUiState(
                    currenciesList = currencies
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EntitiesUiState()
            )

    val entitiesUiState3: StateFlow<EntitiesUiState> =
        payeesRepository.getAllActivePayeesStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { payees ->
                EntitiesUiState(
                    payeesList = payees
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EntitiesUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    suspend fun saveCategory() {
        Log.d("DEBUG", "saveCategory: Called!")
        if(validateCategoryInput()) {
            Log.d("DEBUG", "saveCategory: Input Valid!")
            categoriesRepository.insertCategory(categoryUiState.categoryDetails.toCategory())
        }
    }

    private fun validateCategoryInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.categName)
        return with(uiState) {
            categName.isNotBlank()
        }
    }
    fun updateCategoryState(categoryDetails: CategoryDetails) {
        categoryUiState =
            CategoryUiState(categoryDetails = categoryDetails, isEntryValid = validateCategoryInput(categoryDetails))
    }
    var payeeUiState by mutableStateOf(PayeeUiState())
        private set
    suspend fun savePayee() {
        Log.d("DEBUG", "saveCategory: Called!")
        if(validatePayeeInput()) {
            Log.d("DEBUG", "saveCategory: Input Valid!")
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }

    private fun validatePayeeInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.payeeName)
        return with(uiState) {
            payeeName.isNotBlank()
        }
    }
    fun updatePayeeState(payeeDetails: PayeeDetails) {
        payeeUiState =
            PayeeUiState(payeeDetails = payeeDetails, isEntryValid = validatePayeeInput(payeeDetails))
    }
    var currencyUiState by mutableStateOf(CurrencyUiState())
        private set
    suspend fun saveCurrency() {
        Log.d("DEBUG", "saveCurrency: Called!")
        if(validateCurrencyInput()) {
            Log.d("DEBUG", "saveCurrency: Input Valid!")
            currencyFormatsRepository.insertCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }

    private fun validateCurrencyInput(uiState: CurrencyDetails = currencyUiState.currencyDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.currencyName)
        return with(uiState) {
            currencyName.isNotBlank()
        }
    }
    fun updateCurrencyState(currencyDetails: CurrencyDetails) {
        currencyUiState =
            CurrencyUiState(currencyDetails = currencyDetails, isEntryValid = validateCurrencyInput(currencyDetails))
    }

}

/**
 * Ui State for HomeScreen
 */
data class EntitiesUiState(
    val categoriesList: List<Category> = listOf(),
    val payeesList: List<Payee> = listOf(),
    val currenciesList: List<CurrencyFormat> = listOf(),
)
