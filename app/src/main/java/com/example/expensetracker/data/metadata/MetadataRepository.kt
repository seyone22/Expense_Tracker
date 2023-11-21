package com.example.expensetracker.data.metadata

import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.Flow

interface MetadataRepository {
    suspend fun insertMetadata(info: Metadata)
    suspend fun deleteMetadata(info: Metadata)
    suspend fun updateMetadata(info: Metadata)
    
    fun getAllMetadataStream(): Flow<List<Metadata>>
    fun getMetadataStream(infoId: Int): Flow<Metadata?>
}