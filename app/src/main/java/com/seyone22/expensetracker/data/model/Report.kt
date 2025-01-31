package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "REPORT_V1")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val REPORTID: Int = 0,
    val REPORTNAME: String,
    val GROUPNAME: String?,
    val ACTIVE: Int,
    val SQLCONTENT: String?,
    val LUACONTENT: String?,
    val TEMPLATECONTENT: String?,
    val DESCRIPTION: String?
)