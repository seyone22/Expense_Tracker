package com.example.expensetracker.data.metadata

import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.Flow

class OfflineMetadataRepository(private val metadataDao: MetadataDao) : MetadataRepository {
    override fun getAllMetadataStream(): Flow<List<Metadata>> = metadataDao.getAllMetadata()
    override fun getMetadataStream(info: Int): Flow<Metadata?> = metadataDao.getMetadata(info)

    override suspend fun insertMetadata(info: Metadata) = metadataDao.insert(info)
    override suspend fun deleteMetadata(info: Metadata) = metadataDao.delete(info)
    override suspend fun updateMetadata(info: Metadata) = metadataDao.update(info)
}