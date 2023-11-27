package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SHAREINFO_V1",
    indices = [Index(value = ["CHECKINGACCOUNTID"], name = "IDX_SHAREINFO")],
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["transId"],
            childColumns = ["CHECKINGACCOUNTID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShareInfo(
    @PrimaryKey
    @ColumnInfo(name = "SHAREINFOID")
    val shareInfoId: Long,

    @ColumnInfo(name = "CHECKINGACCOUNTID")
    val checkingAccountId: Long,

    @ColumnInfo(name = "SHARENUMBER")
    val shareNumber: Double?,

    @ColumnInfo(name = "SHAREPRICE")
    val sharePrice: Double?,

    @ColumnInfo(name = "SHARECOMMISSION")
    val shareCommission: Double?,

    @ColumnInfo(name = "SHARELOT")
    val shareLot: String?
)
