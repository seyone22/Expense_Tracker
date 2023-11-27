package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CUSTOMFIELDDATA_V1",
    indices = [Index(value = ["FIELDID", "REFID"], unique = true, name = "IDX_CUSTOMFIELDDATA_REF")]
)
data class CustomFieldData(
    @PrimaryKey
    @ColumnInfo(name = "FIELDATADID")
    val fieldDataId: Long,

    @ColumnInfo(name = "FIELDID")
    val fieldId: Long,

    @ColumnInfo(name = "REFID")
    val refId: Long,

    @ColumnInfo(name = "CONTENT")
    val content: String?
)
