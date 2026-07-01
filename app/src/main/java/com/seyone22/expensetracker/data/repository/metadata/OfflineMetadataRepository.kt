package com.seyone22.expensetracker.data.repository.metadata

import com.seyone22.expensetracker.data.model.AppMetadata
import kotlinx.coroutines.flow.Flow

class OfflineMetadataRepository(private val metadataDao: MetadataDao) : MetadataRepository {
    override fun getAllMetadataStream(): Flow<List<AppMetadata>> = metadataDao.getAllMetadata()
    override fun getMetadataByIdStream(infoId: Int): Flow<AppMetadata?> =
        metadataDao.getMetadataById(infoId)

    override fun getMetadataByNameStream(infoName: String): Flow<AppMetadata?> =
        metadataDao.getMetadataByName(infoName)

    override suspend fun insertMetadata(info: AppMetadata) = metadataDao.insert(info)
    override suspend fun deleteMetadata(info: AppMetadata) = metadataDao.delete(info)
    override suspend fun updateMetadata(info: AppMetadata) = metadataDao.update(info)
}