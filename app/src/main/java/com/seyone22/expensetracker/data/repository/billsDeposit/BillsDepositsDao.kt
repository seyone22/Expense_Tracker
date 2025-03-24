package com.seyone22.expensetracker.data.repository.billsDeposit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.BillsDeposits
import kotlinx.coroutines.flow.Flow

@Dao
interface BillsDepositsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(billsDeposits: BillsDeposits)

    @Update
    suspend fun update(billsDeposits: BillsDeposits)

    @Delete
    suspend fun delete(billsDeposits: BillsDeposits)

    @Query(
        "SELECT " +
                "    BILLSDEPOSITS_V1.*, " +
                "    PAYEE_V1.payeeName AS payeeName, " +
                "    CATEGORY_V1.categName AS categName " +
                "FROM " +
                "    BILLSDEPOSITS_V1 " +
                "LEFT OUTER JOIN " +
                "    PAYEE_V1 ON BILLSDEPOSITS_V1.PAYEEID = PAYEE_V1.payeeId " +
                "LEFT OUTER JOIN " +
                "    CATEGORY_V1 ON BILLSDEPOSITS_V1.CATEGID = CATEGORY_V1.categId "
    )
    fun getAllBillsDeposits(): Flow<List<BillsDepositWithDetails>>

    @Query(
        "SELECT " +
                "    BILLSDEPOSITS_V1.*, " +
                "    PAYEE_V1.payeeName AS payeeName, " +
                "    CATEGORY_V1.categName AS categName " +
                "FROM " +
                "    BILLSDEPOSITS_V1 " +
                "LEFT OUTER JOIN " +
                "    PAYEE_V1 ON BILLSDEPOSITS_V1.PAYEEID = PAYEE_V1.payeeId " +
                "LEFT OUTER JOIN " +
                "    CATEGORY_V1 ON BILLSDEPOSITS_V1.CATEGID = CATEGORY_V1.categId " +
                "WHERE " +
                "    BILLSDEPOSITS_V1.NEXTOCCURRENCEDATE <= CURRENT_TIMESTAMP AND " +
                "    BILLSDEPOSITS_V1.numOccurrences > 0"
    )
    fun getPastDueBillsDeposits(): Flow<List<BillsDepositWithDetails>>
}