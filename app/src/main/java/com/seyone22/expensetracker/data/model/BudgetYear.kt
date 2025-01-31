package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BUDGETYEAR_V1")
data class BudgetYear(
    @PrimaryKey(autoGenerate = true)
    val budgetYearId: Int = 0,
    val budgetYearName: String
)
