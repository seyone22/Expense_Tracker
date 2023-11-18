package com.example.expensetracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.expensetracker.AccountViewModel
import com.example.expensetracker.ExpenseApplication
import com.example.expensetracker.ui.account.AccountEntryViewModel
import com.example.expensetracker.ui.transaction.TransactionEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        //Initializer for AccountEntryViewModel
        initializer {
            AccountEntryViewModel(expenseApplication().container.accountsRepository)
        }
        initializer {
            AccountViewModel(expenseApplication().container.accountsRepository, expenseApplication().container.transactionsRepository)
        }
        initializer {
            TransactionEntryViewModel(expenseApplication().container.transactionsRepository, expenseApplication().container.accountsRepository)
        }
    }
}

fun CreationExtras.expenseApplication(): ExpenseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApplication)