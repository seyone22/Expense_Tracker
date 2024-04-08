package com.example.expensetracker.data.billsDeposit

import com.example.expensetracker.model.BillsDeposits

interface BillsDepositsRepository {
    suspend fun insertBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun deleteBillsDeposit(billsDeposits: BillsDeposits)
    suspend fun updateBillsDeposit(billsDeposits: BillsDeposits)
}