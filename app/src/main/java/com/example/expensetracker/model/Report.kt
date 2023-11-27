package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "REPORT_V1",
    indices = [Index(value = ["REPORTNAME"], name = "INDEX_REPORT_NAME")]
)
data class Report(
    @PrimaryKey
    @ColumnInfo(name = "REPORTID")
    val reportId: Long,

    @ColumnInfo(name = "REPORTNAME")
    val reportName: String,

    @ColumnInfo(name = "GROUPNAME")
    val groupName: String?,

    @ColumnInfo(name = "ACTIVE")
    val active: Int,

    @ColumnInfo(name = "SQLCONTENT")
    val sqlContent: String?,

    @ColumnInfo(name = "LUACONTENT")
    val luaContent: String?,

    @ColumnInfo(name = "TEMPLATECONTENT")
    val templateContent: String?,

    @ColumnInfo(name = "DESCRIPTION")
    val description: String?
)
