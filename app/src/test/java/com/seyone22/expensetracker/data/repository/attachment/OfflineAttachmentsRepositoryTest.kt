package com.seyone22.expensetracker.data.repository.attachment

import com.seyone22.expensetracker.data.model.Attachment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineAttachmentsRepositoryTest {

    @Test
    fun testOfflineAttachmentsRepositoryActions() = runTest {
        val fakeDao = FakeAttachmentDao()
        val repository = OfflineAttachmentsRepository(fakeDao)

        val attachment = Attachment(
            ATTACHMENTID = 1,
            REFTYPE = "Transaction",
            REFID = 101,
            FILENAME = "receipt.jpg",
            DESCRIPTION = "Receipt photo"
        )

        repository.insertAttachment(attachment)
        
        val retrieved = repository.getAttachmentsForRefStream("Transaction", 101).first()
        assertEquals(1, retrieved.size)
        assertEquals("receipt.jpg", retrieved[0].FILENAME)

        repository.deleteAttachment(1)
        val retrievedAfterDelete = repository.getAttachmentsForRefStream("Transaction", 101).first()
        assertEquals(0, retrievedAfterDelete.size)
    }

    class FakeAttachmentDao : AttachmentDao {
        private val list = mutableListOf<Attachment>()

        override suspend fun insert(attachment: Attachment): Long {
            list.add(attachment)
            return attachment.ATTACHMENTID.toLong()
        }

        override suspend fun insertAll(attachments: List<Attachment>) {
            list.addAll(attachments)
        }

        override fun getAttachmentsForRef(refType: String, refId: Int): Flow<List<Attachment>> {
            return flowOf(list.filter { it.REFTYPE == refType && it.REFID == refId })
        }

        override suspend fun delete(attachmentId: Int) {
            list.removeAll { it.ATTACHMENTID == attachmentId }
        }

        override suspend fun deleteAttachmentsForRef(refType: String, refId: Int) {
            list.removeAll { it.REFTYPE == refType && it.REFID == refId }
        }
    }
}
