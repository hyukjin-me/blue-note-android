package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotePageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notePage: NotePage)

    @Update
    fun update(notePage: NotePage)

    @Query("DELETE FROM note_page_table WHERE note_id = :noteId AND id = :pageItemId")
    fun deleteNotePageItem(pageItemId: Long, noteId: Long)

    @Query("SELECT * FROM note_page_table WHERE note_id = :noteId ORDER BY id ASC")
    fun getAll(noteId: Long): LiveData<List<NotePage>>

    @Query("SELECT * FROM note_page_table WHERE note_id = :noteId AND id = :pageItemId LIMIT 1")
    fun getItem(pageItemId: Long, noteId: Long): NotePage
}