package com.seyone22.expensetracker.data.repository.splitTransaction

import com.seyone22.expensetracker.data.model.SplitTransaction
import kotlinx.coroutines.flow.Flow

interface SplitTransactionsRepository {
    suspend fun insertSplit(split: SplitTransaction)
    suspend fun insertAllSplits(splits: List<SplitTransaction>)
    suspend fun deleteSplitsForTransaction(transId: Int)
    fun getSplitsForTransaction(transId: Int): Flow<List<SplitTransaction>>
    suspend fun getSplitsForTransactionDirect(transId: Int): List<SplitTransaction>
}
