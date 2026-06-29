package com.seyone22.expensetracker.data.repository.attachment

import androidx.room.*
import com.seyone22.expensetracker.data.model.Attachment
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: Attachment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attachments: List<Attachment>)

    @Query("SELECT * FROM ATTACHMENT_V1 WHERE REFTYPE = :refType AND REFID = :refId")
    fun getAttachmentsForRef(refType: String, refId: Int): Flow<List<Attachment>>

    @Query("DELETE FROM ATTACHMENT_V1 WHERE ATTACHMENTID = :attachmentId")
    suspend fun delete(attachmentId: Int)

    @Query("DELETE FROM ATTACHMENT_V1 WHERE REFTYPE = :refType AND REFID = :refId")
    suspend fun deleteAttachmentsForRef(refType: String, refId: Int)
}
