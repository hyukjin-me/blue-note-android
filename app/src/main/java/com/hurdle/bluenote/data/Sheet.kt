package com.hurdle.bluenote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sheet")
data class Sheet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    var title: String = "No title",

    @ColumnInfo(name = "start_number")
    var start: Int = 0,

    @ColumnInfo(name = "end_number")
    var end: Int = 0,

    @ColumnInfo(name = "create_time")
    val createTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "total_time")
    var totalTime: String = ""
)