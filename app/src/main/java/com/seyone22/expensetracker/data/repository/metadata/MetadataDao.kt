package com.seyone22.expensetracker.data.repository.metadata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.AppMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface MetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: AppMetadata)

    @Update
    suspend fun update(info: AppMetadata)

    @Delete
    suspend fun delete(info: AppMetadata)

    @Query("SELECT * FROM INFOTABLE_V1 WHERE infoId = :infoId")
    fun getMetadataById(infoId: Int): Flow<AppMetadata?>

    @Query("SELECT * FROM INFOTABLE_V1 WHERE infoName = :infoName")
    fun getMetadataByName(infoName: String): Flow<AppMetadata?>

    @Query("SELECT * FROM INFOTABLE_V1 ORDER BY infoName ASC")
    fun getAllMetadata(): Flow<List<AppMetadata>>
}