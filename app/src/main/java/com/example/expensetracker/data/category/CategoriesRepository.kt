package com.example.expensetracker.data.category

import com.example.expensetracker.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesStream(categId: Int): Flow<Category?>
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getAllActiveCategoriesStream() : Flow<List<Category>>

    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(category: Category)

    fun getAllParentCategories() : Flow<List<Category>>
    fun getAllSubCategories() : Flow<List<Category>>
}