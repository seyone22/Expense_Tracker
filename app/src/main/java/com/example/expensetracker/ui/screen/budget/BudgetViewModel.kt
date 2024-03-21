package com.example.expensetracker.ui.screen.budget

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.payee.PayeesRepository

/**
 * ViewModel to retrieve all items in the Room database.
 */
class BudgetViewModel(

    private val payeesRepository: PayeesRepository,

) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}