package com.seyone22.expensetracker.data.repository.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag)

    @Update
    suspend fun update(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("SELECT * FROM TAGS_V1 WHERE tagName = :tagName")
    fun getTagByName(tagName: String): Flow<Tag?>

    @Query("SELECT * FROM TAGS_V1 WHERE tagName = :tagId")
    fun getTagById(tagId: Int): Flow<Tag?>

    @Query("SELECT * FROM TAGS_V1")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM TAGS_V1 WHERE active = 1")
    fun getActiveTags(): Flow<List<Tag>>
}
