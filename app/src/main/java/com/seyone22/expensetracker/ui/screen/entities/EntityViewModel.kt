package com.seyone22.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.seyone22.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CategoryDetails
import com.seyone22.expensetracker.data.model.CategoryUiState
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.CurrencyHistory
import com.seyone22.expensetracker.data.model.InfoEuroCurrencyHistoryResponse
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.tag.TagsRepository
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.CurrencyUiState
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeUiState
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
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val tagsRepository: TagsRepository
) : ViewModel() {

    // Flows for each type of entity
    private val categoriesParentFlow: Flow<List<Category>> =
        categoriesRepository.getAllParentCategories()
    private val categoriesSubFlow: Flow<List<Category>> = categoriesRepository.getAllSubCategories()

    private val currencyFormatsFlow: Flow<List<CurrencyFormat>> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
    private val currencyHistoryFlow: Flow<List<CurrencyHistory>> =
        currencyHistoryRepository.getAllCurrencyHistoryStream()
    private val payeesFlow: Flow<List<Payee>> = payeesRepository.getAllActivePayeesStream()
    val activeCurrenciesFlow: Flow<List<Int>> = currencyFormatsRepository.getActiveCurrencies()

    // Combine the flows and calculate the totals
    val entitiesUiState: Flow<EntitiesUiState> = combine(
        categoriesParentFlow,
        categoriesSubFlow,
        currencyFormatsFlow,
        currencyHistoryFlow,
        payeesFlow,
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

    suspend fun saveCategory(category: Category) {
        if (category.categName != null) {
            Log.d("TAG", "saveCategory: $category")

            try {
                categoriesRepository.insertCategory(category)
            } catch (e: Exception) {
                Log.d("TAG", "saveCategory: $e")
            }
        }
    }

    suspend fun editCategory(category: Category) {
        if (category.categName != null) {
            categoriesRepository.updateCategory(category)
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
        categoryUiState = CategoryUiState(
            categoryDetails = categoryDetails, isEntryValid = validateCategoryInput(categoryDetails)
        )
    }

    // Payee state and operations
    var payeeUiState by mutableStateOf(PayeeUiState())
        private set
    var selectedPayee by mutableStateOf(Payee())

    suspend fun savePayee(payee: Payee) {
        if (payee.payeeName.isNotBlank()) {
            payeesRepository.insertPayee(payee)
        }
    }

    suspend fun editPayee(payee: Payee) {
        if (payee.payeeName.isNotBlank()) {
            payeesRepository.updatePayee(payee)
        }
    }

    suspend fun saveTag(tag: Tag) {
        if (tag.tagName.isNotBlank()) {
            tagsRepository.insertTag(tag)
        }
    }

    suspend fun editTag(tag: Tag) {
        if (tag.tagName.isNotBlank()) {
            tagsRepository.insertTag(tag)
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
        payeeUiState = PayeeUiState(
            payeeDetails = payeeDetails, isEntryValid = validatePayeeInput(payeeDetails)
        )
    }

    // Currency state and operations
    var currencyUiState by mutableStateOf(CurrencyUiState())
        private set
    var selectedCurrency by mutableStateOf(CurrencyFormat())

    suspend fun saveCurrency(currency: CurrencyFormat) {
        if (currency.currencyName.isNotBlank() && currency.currency_symbol.isNotBlank()) {
            currencyFormatsRepository.insertCurrencyFormat(currency)
        }
    }

    suspend fun editCurrency(currency: CurrencyFormat) {
        if (currency.currencyName.isNotBlank() && currency.currency_symbol.isNotBlank()) {
            currencyFormatsRepository.updateCurrencyFormat(currency)
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
        currencyUiState = CurrencyUiState(
            currencyDetails = currencyDetails, isEntryValid = validateCurrencyInput(currencyDetails)
        )
    }

    suspend fun getNameOfCategory(categId: Int): Category {
        val x = categoriesRepository.getCategoriesStream(categId)
        return x.first() ?: Category()
    }


    suspend fun makeChartModel(currencySymbol: String): Map<LocalDate, Float> {
        val onlineData = getCurrencyHistory(currencySymbol)

        return if (!onlineData.isNullOrEmpty()) {
            val data: Map<LocalDate, Float> = onlineData.associate {
                    LocalDate.parse(
                        it.dateStart, DateTimeFormatter.ofPattern("dd/MM/yyyy")
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
    val categoriesParent: List<Category> = emptyList(),
    val categoriesSub: List<Category> = emptyList(),
    val payeesList: List<Payee> = emptyList(),
    val currenciesList: Pair<List<CurrencyFormat>, List<CurrencyHistory>?> = Pair(
        emptyList(), emptyList()
    ),
    val activeCurrencies: List<Int> = emptyList()
)

// Enum for entity types
enum class Entity(val displayName: String, val pluralDisplayName: String) {
    CATEGORY("Category", "Categories"), PAYEE("Payee", "Payees"), CURRENCY("Currency", "Currencies")
}