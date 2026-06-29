package com.seyone22.expensetracker.ui.screen.reconcile

import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
import com.seyone22.expensetracker.data.repository.transaction.ExpensePerDay
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReconcileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private fun createTestTransaction(
        transId: Int,
        accountId: Int,
        transAmount: Double,
        transCode: String,
        status: String,
        payeeName: String? = null,
        toAccountId: Int? = null,
        toTransAmount: Double? = null
    ): TransactionWithDetails {
        return TransactionWithDetails(
            transId = transId,
            accountId = accountId,
            toAccountId = toAccountId,
            payeeId = 1,
            transCode = transCode,
            transAmount = transAmount,
            status = status,
            transactionNumber = "",
            notes = "",
            categoryId = 1,
            transDate = "2026-06-01",
            lastUpdatedTime = "",
            deletedTime = "",
            followUpId = null,
            toTransAmount = toTransAmount,
            color = 0,
            payeeName = payeeName,
            categName = ""
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testReconciliationInitialization() = runTest(testDispatcher) {
        val fakeAccountsRepository = FakeAccountsRepository()
        val fakeTransactionsRepository = FakeTransactionsRepository()

        val account = Account(
            accountId = 1,
            accountName = "Test Account",
            initialBalance = 100.0,
            accountType = "Checking",
            favoriteAccount = "FALSE",
            currencyId = 1,
            status = "Open"
        )
        fakeAccountsRepository.addAccount(account)

        val trans1 = createTestTransaction(
            transId = 1,
            accountId = 1,
            payeeName = "Payee A",
            transAmount = 25.0,
            transCode = "Withdrawal",
            status = "Reconciled"
        )
        val trans2 = createTestTransaction(
            transId = 2,
            accountId = 1,
            payeeName = "Payee B",
            transAmount = 50.0,
            transCode = "Deposit",
            status = "Unreconciled"
        )
        fakeTransactionsRepository.addTransaction(trans1)
        fakeTransactionsRepository.addTransaction(trans2)

        val viewModel = ReconcileViewModel(fakeAccountsRepository, fakeTransactionsRepository)
        viewModel.initReconciliation(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Test Account", state.account.accountName)
        // Starting balance = initial (100.0) + reconciled withdrawal (-25.0) = 75.0
        assertEquals(75.0, state.startingBalance, 0.01)
        // 1 unreconciled transaction
        assertEquals(1, state.unreconciledTransactions.size)
        assertEquals(2, state.unreconciledTransactions[0].transId)
    }

    @Test
    fun testToggleTransactionCheckedAndDifference() = runTest(testDispatcher) {
        val fakeAccountsRepository = FakeAccountsRepository()
        val fakeTransactionsRepository = FakeTransactionsRepository()

        val account = Account(accountId = 1, accountName = "Test Account", initialBalance = 100.0)
        fakeAccountsRepository.addAccount(account)

        val trans = createTestTransaction(
            transId = 10,
            accountId = 1,
            transAmount = 40.0,
            transCode = "Deposit",
            status = "Unreconciled"
        )
        fakeTransactionsRepository.addTransaction(trans)

        val viewModel = ReconcileViewModel(fakeAccountsRepository, fakeTransactionsRepository)
        viewModel.initReconciliation(1)
        advanceUntilIdle()

        viewModel.startWizard(endingBalance = 140.0, statementDate = "2026-06-30")
        // Starting balance is 100.0. Target is 140.0. Current difference is 40.0
        assertEquals(40.0, viewModel.uiState.value.difference, 0.01)

        // Toggle transaction 10
        viewModel.toggleTransactionChecked(10)
        // Selected total becomes 40.0. Clear balance = 100.0 + 40.0 = 140.0. Difference becomes 0.0
        assertEquals(0.0, viewModel.uiState.value.difference, 0.01)
        assertTrue(viewModel.uiState.value.isCompleteEnabled)
    }

    class FakeAccountsRepository : AccountsRepository {
        private val accounts = mutableMapOf<Int, Account>()
        fun addAccount(account: Account) { accounts[account.accountId] = account }
        override fun getAllAccountsStream(): Flow<List<Account>> = flowOf(accounts.values.toList())
        override fun getAllActiveAccountsStream(): Flow<List<Account>> = flowOf(accounts.values.toList())
        override fun getAccountStream(accountId: Int): Flow<Account?> = flowOf(accounts[accountId])
        override fun getAccountsFromTypeStream(accountType: String): Flow<List<Account>> = flowOf(accounts.values.filter { it.accountType == accountType })
        override fun getAccountBalance(accountId: Int, date: String?): Flow<BalanceResult> = flowOf(BalanceResult(accountId, 0.0))
        override suspend fun insertAccount(account: Account) { accounts[account.accountId] = account }
        override suspend fun deleteAccount(account: Account) { accounts.remove(account.accountId) }
        override suspend fun updateAccount(account: Account) { accounts[account.accountId] = account }
    }

    class FakeTransactionsRepository : TransactionsRepository {
        private val transactions = mutableListOf<TransactionWithDetails>()
        fun addTransaction(trans: TransactionWithDetails) { transactions.add(trans) }
        override fun getAllTransactionsStream(sortField: String, sortDirection: String) = flowOf(transactions)
        override fun getTransactionStream(transId: Int): Flow<Transaction?> = flowOf(null)
        override fun getTransactionsFromAccount(accountId: Int) = flowOf(transactions.filter { it.accountId == accountId })
        override fun getAllTransactionsByToAccount(toAccountId: Int) = emptyList<Transaction>()
        override fun getAllTransactionsByCode(transCode: String) = flowOf(emptyList<Transaction>())
        override fun getAllTransactionsByCategory(categoryId: Int, startDate: String?, endDate: String?) = flowOf(emptyList<Transaction>())
        override fun getAllTransactionsByCategoryName(categName: String, startDate: String?, endDate: String?) = flowOf(emptyList<Transaction>())
        override fun getAllTransactionsByPayee(payeeId: String, startDate: String?, endDate: String?) = flowOf(emptyList<Transaction>())
        override fun getTotalExpensesForWeek(weekNumber: Int) = flowOf(emptyList<ExpensePerDay>())
        override fun getBalanceByAccountId() = flowOf(emptyList<BalanceResult>())
        override fun getTotalBalanceByCode(transactionCode: String, status: String) = flowOf(0.0)
        override fun getTotalBalanceByCodeAndDate(transactionCode: String, status: String, month: String?, year: String) = flowOf(0.0)
        override fun getTotalBalanceByCategoryAndDate(categId: Int, status: String, month: String?, year: String) = flowOf(0.0)
        override fun getTotalBalance(status: String) = flowOf(0.0)
        override fun getTotalBalanceByDate(status: String, month: String, year: String) = flowOf(0.0)
        override fun getExpensesForDateRange(startDate: String, endDate: String) = flowOf(emptyList<BalanceResult>())
        override fun getAllRawTransactionsStream() = flowOf(emptyList<Transaction>())
        override suspend fun insertTransaction(transaction: Transaction) = 0L
        override suspend fun deleteTransaction(transaction: Transaction) {}
        override suspend fun updateTransaction(transaction: Transaction) {
            val idx = transactions.indexOfFirst { it.transId == transaction.transId }
            if (idx >= 0) {
                transactions[idx] = transactions[idx].copy(status = transaction.status)
            }
        }
    }
}
