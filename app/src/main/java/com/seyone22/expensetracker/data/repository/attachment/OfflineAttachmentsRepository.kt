package com.seyone22.expensetracker.data.repository.attachment

import com.seyone22.expensetracker.data.model.Attachment
import kotlinx.coroutines.flow.Flow

class OfflineAttachmentsRepository(private val attachmentDao: AttachmentDao) : AttachmentsRepository {
    override suspend fun insertAttachment(attachment: Attachment): Long =
        attachmentDao.insert(attachment)

    override suspend fun insertAll(attachments: List<Attachment>) =
        attachmentDao.insertAll(attachments)

    override fun getAttachmentsForRefStream(refType: String, refId: Int): Flow<List<Attachment>> =
        attachmentDao.getAttachmentsForRef(refType, refId)

    override suspend fun deleteAttachment(attachmentId: Int) =
        attachmentDao.delete(attachmentId)

    override suspend fun deleteAttachmentsForRef(refType: String, refId: Int) =
        attachmentDao.deleteAttachmentsForRef(refType, refId)
}
