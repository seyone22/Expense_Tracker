package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TRANSLINK_V1",
    indices = [
        Index(value = ["LINKTYPE", "LINKRECORDID"], name = "IDX_LINKRECORD"),
        Index(value = ["CHECKINGACCOUNTID"], name = "IDX_CHECKINGACCOUNT")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["transId"],
            childColumns = ["CHECKINGACCOUNTID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionLink(
    @PrimaryKey
    @ColumnInfo(name = "TRANSLINKID")
    val transLinkId: Long,

    @ColumnInfo(name = "CHECKINGACCOUNTID")
    val checkingAccountId: Long,

    @ColumnInfo(name = "LINKTYPE")
    val linkType: String,

    @ColumnInfo(name = "LINKRECORDID")
    val linkRecordId: Long
)
