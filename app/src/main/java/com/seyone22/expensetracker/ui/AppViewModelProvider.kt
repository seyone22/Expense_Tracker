package com.seyone22.expensetracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.seyone22.expensetracker.ExpenseApplication
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.ui.screen.budget.BudgetViewModel
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.home.HomeViewModel
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingViewModel
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailViewModel
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.report.AddReportViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.report.ReportViewModel
import com.seyone22.expensetracker.ui.screen.settings.SettingsViewModel
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                expenseApplication().container.accountsRepository,
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.metadataRepository,
                expenseApplication().container.currenciesRepository,
            )
        }
        initializer {
            EntityViewModel(
                expenseApplication().container.categoriesRepository,
                expenseApplication().container.payeesRepository,
                expenseApplication().container.currenciesRepository,
                expenseApplication().container.currencyHistoryRepository
            )
        }
        initializer {
            TransactionsViewModel(
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.billsDepositsRepository,
                expenseApplication().container.accountsRepository,
                expenseApplication().container.currenciesRepository
            )
        }
        initializer {
            BudgetViewModel(expenseApplication().container.payeesRepository)
        }
        initializer {
            ReportViewModel(
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.categoriesRepository,
                expenseApplication().container.payeesRepository,
                expenseApplication().container.reportsRepository
            )
        }
        //Initializer for AccountEntryViewModel
        initializer {
            AccountEntryViewModel(
                expenseApplication().container.accountsRepository,
                expenseApplication().container.currenciesRepository,
                expenseApplication().container.metadataRepository
            )
        }
        initializer {
            TransactionEntryViewModel(
                expenseApplication().container.transactionsRepository,
                expenseApplication().container.accountsRepository,
                expenseApplication().container.payeesRepository,
                expenseApplication().container.categoriesRepository,
                expenseApplication().container.billsDepositsRepository
            )
        }
        initializer {

            AccountDetailViewModel(
                expenseApplication().container.accountsRepository,
                expenseApplication().container.transactionsRepository,
            )
        }
        // Initializers for Entity type Viewmodels
        initializer {
            CurrencyEntryViewModel(expenseApplication().container.currenciesRepository)
        }
        initializer {
            PayeeEntryViewModel(expenseApplication().container.payeesRepository)
        }
        // Initializer for Settings ViewModel
        initializer {
            SettingsViewModel(
                expenseApplication().container.metadataRepository,
                expenseApplication().container.currenciesRepository,
                expenseApplication().container.currencyHistoryRepository
            )
        }
        // Initializer for Onboarding ViewModel
        initializer {
            OnboardingViewModel(
                expenseApplication().container.metadataRepository,
                expenseApplication().container.currenciesRepository,
                expenseApplication().container.categoriesRepository
            )
        }
        initializer {
            AddReportViewModel(
                expenseApplication().container.reportsRepository,
                expenseApplication().container.currenciesRepository
            )
        }
        initializer {
            SharedViewModel(
                expenseApplication().container.metadataRepository,
                expenseApplication().container.currenciesRepository
            )
        }
    }
}

fun CreationExtras.expenseApplication(): ExpenseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApplication)