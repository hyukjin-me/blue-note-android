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

    @ColumnInfo(name = "select_a")
    var selectA: Boolean = false,

    @ColumnInfo(name = "select_b")
    var selectB: Boolean = false,

    @ColumnInfo(name = "select_c")
    var selectC: Boolean = false,

    @ColumnInfo(name = "select_d")
    var selectD: Boolean = false,

    @ColumnInfo(name = "select_e")
    var selectE: Boolean = false
)