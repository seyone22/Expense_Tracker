package com.seyone22.expensetracker.data.repository.tag

import com.seyone22.expensetracker.data.model.Tag
import kotlinx.coroutines.flow.Flow

class OfflineTagsRepository(private val tagDao: TagDao) : TagsRepository {
    override fun getTagByNameStream(tagName: String): Flow<Tag?> =
        tagDao.getTagByName(tagName)

    override fun getTagByIdStream(tagId: Int): Flow<Tag?> =
        tagDao.getTagById(tagId)

    override fun getAllTagsStream(): Flow<List<Tag>> = tagDao.getAllTags()

    override fun getAllActiveTagsStream(): Flow<List<Tag?>> =
        tagDao.getActiveTags()

    override suspend fun insertTag(tag: Tag) = tagDao.insert(tag)
    override suspend fun deleteTag(tag: Tag) = tagDao.delete(tag)
    override suspend fun updateTag(tag: Tag) = tagDao.update(tag)
}