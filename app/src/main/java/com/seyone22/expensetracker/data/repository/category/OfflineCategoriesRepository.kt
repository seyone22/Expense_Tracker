package com.seyone22.expensetracker.data.repository.category

import com.seyone22.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow

class OfflineCategoriesRepository(private val categoryDao: CategoryDao) : CategoriesRepository {
    override fun getCategoryByIdStream(categId: Int): Flow<Category?> =
        categoryDao.getCategory(categId)

    override fun getAllCategoriesStream(): Flow<List<Category>> = categoryDao.getAllCategories()
    override fun getAllActiveCategoriesStream(): Flow<List<Category>> =
        categoryDao.getAllActiveCategories()

    override suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    override suspend fun updateCategory(category: Category) = categoryDao.update(category)

    override fun getAllParentCategories(): Flow<List<Category>> =
        categoryDao.getAllParentCategories()

    override fun getAllSubCategories(): Flow<List<Category>> = categoryDao.getAllSubCategories()

    override suspend fun insertIfNotExists(category: Category) =
        categoryDao.insertIfNotExists(category)
}