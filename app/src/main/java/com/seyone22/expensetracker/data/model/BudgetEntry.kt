package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BUDGETTABLE_V1")
data class BudgetEntry(
    @PrimaryKey(autoGenerate = true)
    val budgetEntryId: Int = 0,
    val budgetYearId: Int?,
    val categId: Int?,
    val period: String, // Use a String or Enum for period
    val amount: Double,
    val notes: String?,
    val active: Boolean? = true // Assuming active is a boolean
)
