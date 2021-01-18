package com.hurdle.bluenote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "sheet_id")
    val sheetId: Long = 0L,

    val number: Int = 0,

    @ColumnInfo(name = "is_answer")
    var isAnswer: Boolean = true,

    var time: String = "00:00",

    @ColumnInfo(name = "selector_a")
    var a: Boolean = false,

    @ColumnInfo(name = "selector_b")
    var b: Boolean = false,

    @ColumnInfo(name = "selector_c")
    var c: Boolean = false,

    @ColumnInfo(name = "selector_d")
    var d: Boolean = false,

    @ColumnInfo(name = "selector_e")
    var e: Boolean = false
)