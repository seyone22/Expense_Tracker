package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategoryDetails
import com.example.expensetracker.data.model.CategoryUiState
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.CurrencyHistory
import com.example.expensetracker.data.model.InfoEuroCurrencyHistoryResponse
import com.example.expensetracker.data.model.Payee
import com.example.expensetracker.data.model.toCategory
import com.example.expensetracker.data.repository.category.CategoriesRepository
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.example.expensetracker.data.repository.payee.PayeesRepository
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyUiState
import com.example.expensetracker.ui.screen.operations.entity.currency.toCurrency
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeUiState
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel to retrieve all items in the Room database.
 */
class EntityViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository
) : ViewModel() {

    // Flows for each type of entity
    private val categoriesParentFlow: Flow<List<Category>> =
        categoriesRepository.getAllParentCategories()
    private val categoriesSubFlow: Flow<List<Category>> = categoriesRepository.getAllSubCategories()

    private val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    private val currencyFormatsFlow: Flow<List<CurrencyFormat>> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
    private val currencyHistoryFlow: Flow<List<CurrencyHistory>> = currencyHistoryRepository.getAllCurrencyHistoryStream()
    private val payeesFlow: Flow<List<Payee>> = payeesRepository.getAllActivePayeesStream()

    // Combine the flows and calculate the totals
    val entitiesUiState: Flow<EntitiesUiState> = combine(
        categoriesParentFlow,
        categoriesSubFlow,
        currencyFormatsFlow,
        currencyHistoryFlow,
        payeesFlow
    ) { categoriesParent, categoriesSub, currencies, currencyHistory, payees ->
        EntitiesUiState(categoriesParent, categoriesSub, payees, Pair(currencies, currencyHistory))
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Category state and operations
    var categoryUiState by mutableStateOf(CategoryUiState())
        private set
    var selectedCategory by mutableStateOf(Category())

    suspend fun saveCategory() {
        if (validateCategoryInput()) {
            categoriesRepository.insertCategory(categoryUiState.categoryDetails.toCategory())
        }
    }

    suspend fun editCategory() {
        if (validateCategoryInput()) {
            categoriesRepository.updateCategory(categoryUiState.categoryDetails.toCategory())
        }
    }

    suspend fun deleteCategory(category: Category) {
        categoriesRepository.deleteCategory(category)
    }

    private fun validateCategoryInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
        return with(uiState) {
            categName.isNotBlank()
        }
    }

    fun updateCategoryState(categoryDetails: CategoryDetails) {
        categoryUiState =
            CategoryUiState(
                categoryDetails = categoryDetails,
                isEntryValid = validateCategoryInput(categoryDetails)
            )
    }

    // Payee state and operations
    var payeeUiState by mutableStateOf(PayeeUiState())
        private set
    var selectedPayee by mutableStateOf(Payee())

    suspend fun savePayee() {
        if (validatePayeeInput()) {
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }

    suspend fun editPayee() {
        if (validatePayeeInput()) {
            payeesRepository.updatePayee(payeeUiState.payeeDetails.toPayee())
        }
    }

    suspend fun deletePayee(payee: Payee) {
        payeesRepository.deletePayee(payee)
    }

    private fun validatePayeeInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
        return with(uiState) {
            payeeName.isNotBlank()
        }
    }

    fun updatePayeeState(payeeDetails: PayeeDetails) {
        payeeUiState =
            PayeeUiState(
                payeeDetails = payeeDetails,
                isEntryValid = validatePayeeInput(payeeDetails)
            )
    }

    // Currency state and operations
    var currencyUiState by mutableStateOf(CurrencyUiState())
        private set
    var selectedCurrency by mutableStateOf(CurrencyFormat())

    suspend fun saveCurrency() {
        if (validateCurrencyInput()) {
            currencyFormatsRepository.insertCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }

    suspend fun editCurrency() {
        if (validateCurrencyInput()) {
            currencyFormatsRepository.updateCurrencyFormat(currencyUiState.currencyDetails.toCurrency())
        }
    }

    suspend fun deleteCurrency(currency: CurrencyFormat) {
        currencyFormatsRepository.deleteCurrencyFormat(currency)
    }

    private fun validateCurrencyInput(uiState: CurrencyDetails = currencyUiState.currencyDetails): Boolean {
        return with(uiState) {
            currencyName.isNotBlank()
        }
    }

    fun updateCurrencyState(currencyDetails: CurrencyDetails) {
        currencyUiState =
            CurrencyUiState(
                currencyDetails = currencyDetails,
                isEntryValid = validateCurrencyInput(currencyDetails)
            )
    }

    suspend fun getNameOfCategory(categId: Int): Category {
        val x = categoriesRepository.getCategoriesStream(categId)
        return x.first() ?: Category()
    }


    suspend fun makeChartModel(currencySymbol: String): Map<LocalDate, Float> {
        val onlineData = getCurrencyHistory(currencySymbol)

        return if (!onlineData.isNullOrEmpty()) {
            val data: Map<LocalDate, Float> = onlineData
                .associate {
                    LocalDate.parse(
                        it.dateStart,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ) to it.amount.toFloat()
                }
            data
        } else {
            Log.d("ERROR", "makeChartModel: Empty or null online data")
            mapOf()
        }
    }

    private suspend fun getCurrencyHistory(currencySymbol: String): List<InfoEuroCurrencyHistoryResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val response =
                    InfoEuroApi.retrofitService.getCurrencyHistory("https://ec.europa.eu/budg/inforeuro/api/public/currencies/$currencySymbol")
                response.body()
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Failed to fetch currency history: $e")
            null
        }
    }
}

/**
 * Ui State for EntitiesScreen
 */
data class EntitiesUiState(
    val categoriesParent: List<Category> = listOf(),
    val categoriesSub: List<Category> = listOf(),
    val payeesList: List<Payee> = listOf(),
    val currenciesList: Pair<List<CurrencyFormat>, List<CurrencyHistory>?> = Pair(listOf(), listOf()),
)

// Enum for entity types
enum class Entity(val displayName: String, val pluralDisplayName: String) {
    CATEGORY("Category", "Categories"),
    PAYEE("Payee", "Payees"),
    CURRENCY("Currency", "Currencies")
}