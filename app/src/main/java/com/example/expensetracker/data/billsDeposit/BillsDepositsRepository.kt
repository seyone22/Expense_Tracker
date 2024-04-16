package com.example.expensetracker.data.billsDeposit

import com.example.expensetracker.model.BillsDepositWithDetails
import com.example.expensetracker.model.BillsDeposits
import com.example.expensetracker.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

interface BillsDepositsRepository {
    suspend fun insertBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun deleteBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun updateBillsDeposit(billsDeposits: BillsDeposits)

    fun getAllTransactionsStream(): Flow<List<BillsDepositWithDetails>>
}