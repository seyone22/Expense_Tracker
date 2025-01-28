package com.example.expensetracker.data.repository.tagLink

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.expensetracker.data.model.TagLink

@Dao
interface TagLinkDao {
    @Insert
    suspend fun insertTagLink(tagLink: TagLink)

    @Query("SELECT * FROM TAGLINK_V1 WHERE refType = :refType AND refId = :refId")
    suspend fun getTagsForReference(refType: String, refId: Int): List<TagLink>
}
