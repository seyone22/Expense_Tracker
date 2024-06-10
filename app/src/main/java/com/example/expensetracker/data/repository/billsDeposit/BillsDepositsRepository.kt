package com.example.expensetracker.data.repository.billsDeposit

import com.example.expensetracker.data.model.BillsDepositWithDetails
import com.example.expensetracker.data.model.BillsDeposits
import kotlinx.coroutines.flow.Flow

interface BillsDepositsRepository {
    suspend fun insertBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun deleteBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun updateBillsDeposit(billsDeposits: BillsDeposits)

    fun getAllTransactionsStream(): Flow<List<BillsDepositWithDetails>>
}