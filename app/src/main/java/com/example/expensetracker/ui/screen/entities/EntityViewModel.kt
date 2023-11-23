package com.example.expensetracker.ui.screen.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
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
}

/**
 * Ui State for HomeScreen
 */
data class EntitiesUiState(
    val categoriesList: List<Category> = listOf(),
    val payeesList: List<Payee> = listOf(),
    val currenciesList: List<CurrencyFormat> = listOf(),
)
