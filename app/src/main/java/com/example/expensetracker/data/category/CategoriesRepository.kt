package com.example.expensetracker.data.category

import com.example.expensetracker.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getAllActiveCategoriesStream() : Flow<List<Category>>
    fun getCategoriesStream(categId: Int): Flow<Category?>
    fun getCategoriesFromTypeStream(categId: Int): Flow<List<Category>>

    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(account: Category)
}