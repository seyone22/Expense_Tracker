package com.seyone22.expensetracker.data.repository.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.Tag

@Dao
interface TagDao {
    @Insert
    suspend fun insertTag(tag: Tag)

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM TAGS_V1 WHERE active = 1")
    suspend fun getActiveTags(): List<Tag>
}
