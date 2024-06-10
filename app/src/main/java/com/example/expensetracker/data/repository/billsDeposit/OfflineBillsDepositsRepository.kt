package com.example.expensetracker.data.repository.billsDeposit

import com.example.expensetracker.data.model.BillsDepositWithDetails
import com.example.expensetracker.data.model.BillsDeposits
import kotlinx.coroutines.flow.Flow

class OfflineBillsDepositsRepository(private val billsDepositsDao: BillsDepositsDao) :
    BillsDepositsRepository {
    override suspend fun insertBillsDeposit(billsDeposits: BillsDeposits) =
        billsDepositsDao.insert(billsDeposits)

    override suspend fun deleteBillsDeposit(billsDeposits: BillsDeposits) =
        billsDepositsDao.delete(billsDeposits)

    override suspend fun updateBillsDeposit(billsDeposits: BillsDeposits) =
        billsDepositsDao.update(billsDeposits)

    override fun getAllTransactionsStream(): Flow<List<BillsDepositWithDetails>> =
        billsDepositsDao.getAllTransactions()
}