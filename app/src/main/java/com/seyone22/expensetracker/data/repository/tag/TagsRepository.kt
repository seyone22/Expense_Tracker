package com.seyone22.expensetracker.data.repository.tag

import com.seyone22.expensetracker.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    fun getTagByIdStream(tagId: Int): Flow<Tag?>
    fun getTagByNameStream(tagName: String): Flow<Tag?>
    fun getAllTagsStream(): Flow<List<Tag>>
    fun getAllActiveTagsStream(): Flow<List<Tag?>>

    suspend fun insertTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)
    suspend fun updateTag(tag: Tag)
}