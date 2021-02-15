package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_page_table WHERE note_id = :noteId")
    fun deleteNotePages(noteId: Long)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY id DESC LIMIT 6")
    fun getRecentNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE id = :key LIMIT 1")
    fun get(key: Long): Note

    @Query("SELECT * FROM note_table WHERE title LIKE :text")
    fun search(text: String): List<Note>
}