package com.example.expensetracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.expensetracker.ExpenseApplication
import com.example.expensetracker.ui.screen.accounts.AccountViewModel
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.operations.account.AccountDetailViewModel
import com.example.expensetracker.ui.screen.operations.account.AccountEntryViewModel
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryEntryViewModel
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryViewModel
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.report.ReportViewModel
import com.example.expensetracker.ui.screen.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AccountViewModel(
                expenseApplication().container.accountsRepository,
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.metadataRepository,
                expenseApplication().container.currenciesRepository
            )
        }
        initializer {
            EntityViewModel(
                expenseApplication().container.categoriesRepository,
                expenseApplication().container.payeesRepository,
                expenseApplication().container.currenciesRepository
            )
        }
        initializer {
            ReportViewModel(expenseApplication().container.payeesRepository)
        }
        //Initializer for AccountEntryViewModel
        initializer {
            AccountEntryViewModel(expenseApplication().container.accountsRepository)
        }
        initializer {
            TransactionEntryViewModel(
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.accountsRepository,
                expenseApplication().container.payeesRepository,
                expenseApplication().container.categoriesRepository
            )
        }
        initializer {
            AccountDetailViewModel(
                expenseApplication().container.accountsRepository,
                expenseApplication().container.transactionsRepository
            )
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