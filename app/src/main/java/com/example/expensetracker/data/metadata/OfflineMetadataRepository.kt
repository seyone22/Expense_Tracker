package com.example.expensetracker.data.metadata

import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.Flow

class OfflineMetadataRepository(private val metadataDao: MetadataDao) : MetadataRepository {
    override fun getAllMetadataStream(): Flow<List<Metadata>> = metadataDao.getAllMetadata()
    override fun getMetadataByIdStream(infoId: Int): Flow<Metadata?> = metadataDao.getMetadataById(infoId)
    override fun getMetadataByNameStream(infoName: String): Flow<Metadata?> = metadataDao.getMetadataByName(infoName)

    override suspend fun insertMetadata(info: Metadata) = metadataDao.insert(info)
    override suspend fun deleteMetadata(info: Metadata) = metadataDao.delete(info)
    override suspend fun updateMetadata(info: Metadata) = metadataDao.update(info)
}