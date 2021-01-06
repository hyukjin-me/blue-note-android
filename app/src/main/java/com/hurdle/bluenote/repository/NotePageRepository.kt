package com.hurdle.bluenote.repository

import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.data.NotePageDao

class NotePageRepository(private val notePageDao: NotePageDao, noteId: Long) {
    val getNotePage: LiveData<List<NotePage>> = notePageDao.getAll(noteId)

    fun insert(page: NotePage) {
        notePageDao.insert(page)
    }
}