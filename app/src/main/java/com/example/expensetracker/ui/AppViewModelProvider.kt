package com.example.expensetracker.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.expensetracker.ui.screen.accounts.AccountViewModel
import com.example.expensetracker.ExpenseApplication
import com.example.expensetracker.ui.account.AccountDetailViewModel
import com.example.expensetracker.ui.account.AccountEntryViewModel
import com.example.expensetracker.ui.entity.category.CategoryEntryViewModel
import com.example.expensetracker.ui.entity.currency.CurrencyEntryViewModel
import com.example.expensetracker.ui.entity.payee.PayeeEntryViewModel
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.settings.SettingsViewModel
import com.example.expensetracker.ui.transaction.TransactionEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AccountViewModel(expenseApplication().container.accountsRepository, expenseApplication().container.transactionsRepository)
        }
        initializer {
            EntityViewModel(expenseApplication().container.categoriesRepository, expenseApplication().container.payeesRepository, expenseApplication().container.currenciesRepository)
        }
        //Initializer for AccountEntryViewModel
        initializer {
            AccountEntryViewModel(expenseApplication().container.accountsRepository)
        }
        initializer {
            TransactionEntryViewModel(expenseApplication().container.transactionsRepository, expenseApplication().container.accountsRepository, expenseApplication().container.payeesRepository, expenseApplication().container.categoriesRepository)
        }
        initializer {
            AccountDetailViewModel(expenseApplication().container.accountsRepository, expenseApplication().container.transactionsRepository, SavedStateHandle())
        }
        // Initializers for Entity type Viewmodels
        initializer {
            CategoryEntryViewModel(expenseApplication().container.categoriesRepository)
        }
        initializer {
            CurrencyEntryViewModel(expenseApplication().container.currenciesRepository)
        }
        initializer {
            PayeeEntryViewModel(expenseApplication().container.payeesRepository)
        }
        // Initializer for Settings ViewModel
        initializer {
            SettingsViewModel(expenseApplication().container.payeesRepository)
        }
    }
}

fun CreationExtras.expenseApplication(): ExpenseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApplication)