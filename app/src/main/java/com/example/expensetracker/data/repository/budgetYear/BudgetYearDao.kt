package com.example.expensetracker.data.repository.budgetYear

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.expensetracker.data.model.BudgetYear

@Dao
interface BudgetYearDao {
    @Insert
    suspend fun insertBudgetYear(year: BudgetYear)

    @Query("SELECT * FROM BUDGETYEAR_V1")
    suspend fun getAllBudgetYears(): List<BudgetYear>
}
