package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotePageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notePage: NotePage)

    @Query("SELECT * FROM note_page_table WHERE note_id = :key ORDER BY id ASC")
    fun getAll(key: Long): LiveData<List<NotePage>>
}