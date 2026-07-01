package com.seyone22.expensetracker.data.repository.metadata

import com.seyone22.expensetracker.data.model.AppMetadata
import kotlinx.coroutines.flow.Flow

interface MetadataRepository {
    suspend fun insertMetadata(info: AppMetadata)
    suspend fun deleteMetadata(info: AppMetadata)
    suspend fun updateMetadata(info: AppMetadata)

    fun getAllMetadataStream(): Flow<List<AppMetadata>>
    fun getMetadataByIdStream(infoId: Int): Flow<AppMetadata?>
    fun getMetadataByNameStream(infoName: String): Flow<AppMetadata?>

}