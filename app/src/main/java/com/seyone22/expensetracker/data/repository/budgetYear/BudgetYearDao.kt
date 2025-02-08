package com.seyone22.expensetracker.data.repository.budgetYear

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.BudgetYear
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetYearDao {
    @Insert
    suspend fun insertBudgetYear(budgetYear: BudgetYear)

    @Update
    suspend fun updateBudgetYear(budgetYear: BudgetYear)

    @Delete
    suspend fun deleteBudgetYear(budgetYear: BudgetYear)

    @Query("SELECT * FROM BUDGETYEAR_V1")
    fun getAllBudgetYears(): Flow<List<BudgetYear>>

    @Query("SELECT * FROM BUDGETYEAR_V1 WHERE budgetYearId = :budgetYearId")
    fun getBudgetYearById(budgetYearId: Int): Flow<BudgetYear?>

    @Query("SELECT * FROM BUDGETYEAR_V1 WHERE budgetYearName = :budgetYearName")
    fun getBudgetYearByName(budgetYearName: String): Flow<BudgetYear?>
}
