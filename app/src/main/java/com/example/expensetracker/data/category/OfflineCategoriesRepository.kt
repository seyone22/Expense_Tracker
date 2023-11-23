package com.example.expensetracker.data.category

import com.example.expensetracker.model.Category
import kotlinx.coroutines.flow.Flow

class OfflineCategoriesRepository(private val categoryDao: CategoryDao) : CategoriesRepository {
    override fun getAllCategoriesStream(): Flow<List<Category>> = categoryDao.getAllCategories()
    override fun getAllActiveCategoriesStream(): Flow<List<Category>> = categoryDao.getAllActiveCategories()
    override fun getCategoriesStream(categId: Int): Flow<Category?> = categoryDao.getCategory(categId)
    override fun getCategoriesFromTypeStream(categId: Int): Flow<List<Category>> = categoryDao.getAllCategoriesByCategory(categId)

    override suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    override suspend fun updateCategory(category: Category) = categoryDao.update(category)
}