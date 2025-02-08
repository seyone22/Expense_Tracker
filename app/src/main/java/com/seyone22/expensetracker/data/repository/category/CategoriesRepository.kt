package com.seyone22.expensetracker.data.repository.category

import com.seyone22.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesStream(categId: Int): Flow<Category?>
    fun getAllCategoriesStream(): Flow<List<Category>>
    fun getAllActiveCategoriesStream(): Flow<List<Category>>

    fun getAllParentCategories(): Flow<List<Category>>
    fun getAllSubCategories(): Flow<List<Category>>

    suspend fun insertCategory(category: Category)
    suspend fun insertIfNotExists(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(category: Category)
}