package com.example.expensetracker.data.metadata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.Flow

@Dao
interface MetadataDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(info: Metadata)
    @Update
    suspend fun update(info: Metadata)
    @Delete
    suspend fun delete(info: Metadata)

    @Query("SELECT * FROM INFOTABLE_V1 WHERE infoId = :infoId")
    fun getMetadataById(infoId: Int): Flow<Metadata>
    @Query("SELECT * FROM INFOTABLE_V1 WHERE infoName = :infoName")
    fun getMetadataByName(infoName: String): Flow<Metadata>
    @Query("SELECT * FROM INFOTABLE_V1 ORDER BY infoName ASC")
    fun getAllMetadata(): Flow<List<Metadata>>
}