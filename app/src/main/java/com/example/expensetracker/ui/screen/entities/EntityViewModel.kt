package com.example.expensetracker.ui.screen.entities

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    private val currencyFormatsFlow: Flow<List<CurrencyFormat>> = currencyFormatsRepository.getAllCurrencyFormatsStream()
    private val payeesFlow: Flow<List<Payee>> = payeesRepository.getAllActivePayeesStream()

    // Combine the flows and calculate the totals
    val entitiesUiState: Flow<EntitiesUiState> = combine(categoriesFlow, currencyFormatsFlow, payeesFlow) { categories, currencies, payees ->
        EntitiesUiState(categories, payees, currencies)
    }

    val d: StateFlow<EntitiesUiState> =
        categoriesRepository.getAllCategoriesStream()
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

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    suspend fun saveCategory() {
        if(validateCategoryInput()) {
            categoriesRepository.insertCategory(categoryUiState.categoryDetails.toCategory())
        }
    }
    suspend fun editCategory() {
        if(validateCategoryInput()) {
            categoriesRepository.updateCategory(categoryUiState.categoryDetails.toCategory())
        }
    }
    suspend fun deleteCategory(category : Category) {
        categoriesRepository.deleteCategory(category)
    }

    private fun validateCategoryInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
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
        if(validatePayeeInput()) {
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }
    suspend fun editPayee() {
        if(validatePayeeInput()) {
            payeesRepository.updatePayee(payeeUiState.payeeDetails.toPayee())
        }
    }
    suspend fun deletePayee(payee : Payee) {
        payeesRepository.deletePayee(payee)
    }


    private fun validatePayeeInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
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
        if(validateCurrencyInput()) {
            currencyFormatsRepository.insertCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }
    suspend fun editCurrency() {
        if(validateCurrencyInput()) {
            currencyFormatsRepository.updateCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }
    suspend fun deleteCurrency(currency : CurrencyFormat) {
        currencyFormatsRepository.deleteCurrencyFormat(currency)
    }

    private fun validateCurrencyInput(uiState: CurrencyDetails = currencyUiState.currencyDetails): Boolean {
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

enum class Entity(val displayName : String, val pluralDisplayName : String) {
    CATEGORY("Category", "Categories"),
    PAYEE("Payee", "Payees"),
    CURRENCY("Currency", "Currencies")
}