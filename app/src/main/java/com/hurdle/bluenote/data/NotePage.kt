package com.hurdle.bluenote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_page_table")
data class NotePage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "note_id")
    val noteId: Long = 0L,

    var content: String = "",

    @ColumnInfo(name = "time")
    val time: Long = System.currentTimeMillis()
)