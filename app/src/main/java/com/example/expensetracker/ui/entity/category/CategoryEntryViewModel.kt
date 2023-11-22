package com.example.expensetracker.ui.entity.category

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.model.Category

class CategoryEntryViewModel(private val categoriesRepository: CategoriesRepository) : ViewModel() {
    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    fun updateUiState(categoryDetails: CategoryDetails) {
        categoryUiState =
            CategoryUiState(categoryDetails = categoryDetails, isEntryValid = validateInput(categoryDetails))
    }

    suspend fun saveCategory() {
        Log.d("DEBUG", "saveCategory: Called!")
        if(validateInput()) {
            Log.d("DEBUG", "saveCategory: Input Valid!")
            categoriesRepository.insertCategory(categoryUiState.categoryDetails.toCategory())
        }
    }

    private fun validateInput(uiState: CategoryDetails = categoryUiState.categoryDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.categName)
        return with(uiState) {
            categName.isNotBlank()
        }
    }
}

//Data class for CategoryUiState
data class CategoryUiState(
    val categoryDetails: CategoryDetails = CategoryDetails(active = "true", parentId = "", categName = ""),
    val isEntryValid: Boolean = false
)
//Data class for CategoryDetails
data class CategoryDetails(
    val categId : Int = 0,
    val categName : String,
    val active : String,
    val parentId : String
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
