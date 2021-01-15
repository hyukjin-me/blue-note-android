package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questions: List<Question>)

    @Update
    fun update(question: Question)

    @Query("SELECT * FROM question WHERE sheet_id = :key")
    fun getAll(key: Long): LiveData<List<Question>>

    @Query("SELECT * FROM question WHERE sheet_id = :key")
    fun getQuestions(key: Long): List<Question>
}