package com.seyone22.expensetracker.data.repository.splitTransaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seyone22.expensetracker.data.model.SplitTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(split: SplitTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(splits: List<SplitTransaction>)

    @Query("DELETE FROM SPLITTRANSACTIONS_V1 WHERE TRANSID = :transId")
    suspend fun deleteSplitsForTransaction(transId: Int)

    @Query("SELECT * FROM SPLITTRANSACTIONS_V1 WHERE TRANSID = :transId")
    fun getSplitsForTransaction(transId: Int): Flow<List<SplitTransaction>>

    @Query("SELECT * FROM SPLITTRANSACTIONS_V1 WHERE TRANSID = :transId")
    suspend fun getSplitsForTransactionDirect(transId: Int): List<SplitTransaction>
}
