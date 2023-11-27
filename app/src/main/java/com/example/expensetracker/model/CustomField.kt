package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CUSTOMFIELD_V1",
    indices = [Index(value = ["REFTYPE"], name = "IDX_CUSTOMFIELD_REF")]
)
data class CustomField(
    @PrimaryKey
    @ColumnInfo(name = "FIELDID")
    val fieldId: Long,

    @ColumnInfo(name = "REFTYPE")
    val refType: String,

    @ColumnInfo(name = "DESCRIPTION")
    val description: String?,

    @ColumnInfo(name = "TYPE")
    val type: String,

    @ColumnInfo(name = "PROPERTIES")
    val properties: String
)
