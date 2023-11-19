package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class EntityViewModel(
    private val entitiesRepository: PayeesRepository,
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [EntitysRepository] and mapped to
     * [EntityUiState]
     */

    val entitiesUiState: StateFlow<EntitiesUiState> =
        entitiesRepository.getAllPayeesStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { entities ->
                EntitiesUiState(listOf())
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
    val entityList: List<Pair<Payee, Double>> = emptyList(),
    val grandTotal: Double = 0.0
)
