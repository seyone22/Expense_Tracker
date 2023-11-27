package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "BUDGETYEAR_V1",
    indices = [Index(value = ["BUDGETYEARNAME"], name = "IDX_BUDGETYEAR_BUDGETYEARNAME")]
)
data class BudgetYear(
    @PrimaryKey
    @ColumnInfo(name = "BUDGETYEARID")
    val budgetYearId: Long,

    @ColumnInfo(name = "BUDGETYEARNAME")
    val budgetYearName: String
)
