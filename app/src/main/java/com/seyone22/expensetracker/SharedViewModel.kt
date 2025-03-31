package com.seyone22.expensetracker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.budgetEntry.BudgetEntryRepository
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.tag.TagsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.managers.CurrencyManager
import com.seyone22.expensetracker.managers.NotificationState
import com.seyone22.expensetracker.managers.NotificationStateManager
import com.seyone22.expensetracker.managers.NotificationType
import com.seyone22.expensetracker.managers.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SharedViewModel(
    private val metadataRepository: MetadataRepository,
    private val accountsRepository: AccountsRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val billsDepositsRepository: BillsDepositsRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val tagsRepository: TagsRepository,
    private val budgetEntryRepository: BudgetEntryRepository,
) : ViewModel() {
    private val notificationStateManager: NotificationStateManager = NotificationStateManager(
        transactionsRepository, billsDepositsRepository, budgetEntryRepository
    )
    private val currencyManager: CurrencyManager =
        CurrencyManager(currencyFormatsRepository, currencyHistoryRepository)
    private val securityManager: SecurityManager = SecurityManager()

    init {
        viewModelScope.launch {
            notificationStateManager.checkInitialConditions()
        }
    }

    // Streams for UI
    val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    val payeesFlow: Flow<List<Payee>> = payeesRepository.getAllPayeesStream()
    val tagsFlow: Flow<List<Tag>> = tagsRepository.getAllTagsStream()
    val accountsFlow: Flow<List<Account>> = accountsRepository.getAllActiveAccountsStream()

    val baseCurrencyFlow: Flow<CurrencyFormat?> =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")
            .combine(currencyFormatsRepository.getAllCurrencyFormatsStream()) { baseCurrencyId, allCurrencyFormats ->
                allCurrencyFormats.firstOrNull { it.currencyId == baseCurrencyId?.infoValue?.toInt() }
            }

    val isLoading: StateFlow<Boolean> = currencyManager.isUpdating
    val isSecureScreenEnabled: StateFlow<Boolean> = securityManager.isSecureScreenEnabled
    val notifications: StateFlow<NotificationState> get() = notificationStateManager.notifications

    // New: Handle the `isUsed` flow
    val isUsedFlow: Flow<Boolean> = metadataRepository.getMetadataByNameStream("ISUSED")
        .map { metadata -> metadata?.infoValue == "TRUE" }

    // Utility Functions to interact with the repository layer
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.insertTransaction(transaction)
        }
    }

    fun insertBillsDeposit(billsDeposit: BillsDeposits) {
        viewModelScope.launch {
            billsDepositsRepository.insertBillsDeposit(billsDeposit)
        }
    }

    fun insertBudgetEntry(budgetEntry: BudgetEntry) {
        viewModelScope.launch {
            budgetEntryRepository.insertBudgetEntry(budgetEntry)
        }
    }

    // Utility functions to interact with the currency repository layer
    fun getMonthlyRates() {
        viewModelScope.launch {
            currencyManager.getMonthlyRates(baseCurrencyFlow)
        }
    }

    suspend fun getCurrencyById(id: Int): CurrencyFormat? {
        return currencyManager.getCurrencyById(id)
    }


    // Notifications Management
    suspend fun updateNotifications() = notificationStateManager.updateNotifications()
    fun removeNotification(notification: NotificationType) =
        notificationStateManager.removeNotification(notification)

    // Secure Screen Settings
    fun saveSecureScreenSetting(context: Context?, isSecure: Boolean) =
        securityManager.saveSecureScreenSetting(context, isSecure)

    fun getSecureScreenSetting(context: Context) = securityManager.getSecureScreenSetting(context)

    fun nukeAllWorkManagers(context: Context) =
        notificationStateManager.nukeAllWorkManagers(context)
}

