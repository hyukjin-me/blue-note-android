package com.hurdle.bluenote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.data.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val getNotes: LiveData<List<Note>> = noteDao.getAll()

    @WorkerThread
    fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    fun update(note: Note) {
        noteDao.update(note)
    }

    @WorkerThread
    fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    @WorkerThread
    fun deleteNotePage(id: Long){
        noteDao.deleteNotePages(id)
    }

    @WorkerThread
    fun getNote(id: Long) = noteDao.get(id)
}