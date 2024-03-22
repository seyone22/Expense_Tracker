package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "CATEGORY_V1",
    indices = [
        Index(value = ["categId", "parentId"], unique = true),
        Index(value = ["categName"])
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categId : Int = 0,
    val categName : String = "",
    val active : Int = 0,
    val parentId : Int = 0
)



//Data class for CategoryUiState
data class CategoryUiState(
    val categoryDetails: CategoryDetails = CategoryDetails(active = "true", parentId = "", categName = ""),
    val isEntryValid: Boolean = false
)
//Data class for CategoryDetails
data class CategoryDetails(
    val categId : Int = 0,
    var categName : String,
    val active : String = "1",
    val parentId : String = "-1"
)

// Extension functions to convert between [Category], [CategoryUiState], and [CategoryDetails]
fun CategoryDetails.toCategory(): Category = Category(
    categId = categId,
    categName = categName,
    active = active.toInt(),
    parentId = parentId.toInt()
)

fun Category.toCategoryUiState(isEntryValid: Boolean = false): CategoryUiState = CategoryUiState(
    categoryDetails = this.toCategoryDetails(),
    isEntryValid = isEntryValid
)

fun Category.toCategoryDetails(): CategoryDetails = CategoryDetails(
    categId = categId,
    categName = categName,
    active = active.toString(),
    parentId = parentId.toString()
)
