package com.seyone22.expensetracker.data.repository.splitTransaction

import com.seyone22.expensetracker.data.model.SplitTransaction
import kotlinx.coroutines.flow.Flow

class OfflineSplitTransactionsRepository(
    private val splitTransactionDao: SplitTransactionDao
) : SplitTransactionsRepository {
    override suspend fun insertSplit(split: SplitTransaction) = splitTransactionDao.insert(split)
    override suspend fun insertAllSplits(splits: List<SplitTransaction>) = splitTransactionDao.insertAll(splits)
    override suspend fun deleteSplitsForTransaction(transId: Int) = splitTransactionDao.deleteSplitsForTransaction(transId)
    override fun getSplitsForTransaction(transId: Int): Flow<List<SplitTransaction>> = splitTransactionDao.getSplitsForTransaction(transId)
    override suspend fun getSplitsForTransactionDirect(transId: Int): List<SplitTransaction> = splitTransactionDao.getSplitsForTransactionDirect(transId)
}
