package com.example.expensetracker.data.repository.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM CATEGORY_V1 WHERE categId = :categoryId")
    fun getCategory(categoryId: Int): Flow<Category>

    @Query("SELECT * FROM CATEGORY_V1 ORDER BY categName ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_V1 WHERE active = 1 ORDER BY categName ASC")
    fun getAllActiveCategories(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_V1 WHERE parentId = -1 ORDER BY categName ASC")
    fun getAllParentCategories(): Flow<List<Category>>

    @Query("SELECT * FROM CATEGORY_V1 WHERE parentId != -1 ORDER BY categName ASC")
    fun getAllSubCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(category: Category)
}