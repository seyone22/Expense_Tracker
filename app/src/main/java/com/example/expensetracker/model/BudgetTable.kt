package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "BUDGETTABLE_V1",
    indices = [Index(value = ["BUDGETYEARID"], name = "IDX_BUDGETTABLE_BUDGETYEARID")]
)
data class BudgetTable(
    @PrimaryKey
    @ColumnInfo(name = "BUDGETENTRYID")
    val budgetEntryId: Long,

    @ColumnInfo(name = "BUDGETYEARID")
    val budgetYearId: Long,

    @ColumnInfo(name = "CATEGID")
    val categoryId: Long?,

    @ColumnInfo(name = "PERIOD")
    val period: String,

    @ColumnInfo(name = "AMOUNT")
    val amount: Double,

    @ColumnInfo(name = "NOTES")
    val notes: String?,

    @ColumnInfo(name = "ACTIVE")
    val active: Int
)
