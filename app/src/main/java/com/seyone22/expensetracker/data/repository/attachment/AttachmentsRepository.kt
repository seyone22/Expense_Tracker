package com.seyone22.expensetracker.data.repository.attachment

import com.seyone22.expensetracker.data.model.Attachment
import kotlinx.coroutines.flow.Flow

interface AttachmentsRepository {
    suspend fun insertAttachment(attachment: Attachment): Long
    suspend fun insertAll(attachments: List<Attachment>)
    fun getAttachmentsForRefStream(refType: String, refId: Int): Flow<List<Attachment>>
    suspend fun deleteAttachment(attachmentId: Int)
    suspend fun deleteAttachmentsForRef(refType: String, refId: Int)
}
