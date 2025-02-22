package com.seyone22.expensetracker.data.repository.metadata

import com.seyone22.expensetracker.data.model.Metadata
import kotlinx.coroutines.flow.Flow

interface MetadataRepository {
    suspend fun insertMetadata(info: Metadata)
    suspend fun deleteMetadata(info: Metadata)
    suspend fun updateMetadata(info: Metadata)

    fun getAllMetadataStream(): Flow<List<Metadata>>
    fun getMetadataByIdStream(infoId: Int): Flow<Metadata?>
    fun getMetadataByNameStream(infoName: String): Flow<Metadata?>

}