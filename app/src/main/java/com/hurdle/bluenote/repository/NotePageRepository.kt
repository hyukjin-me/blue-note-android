package com.hurdle.bluenote.repository

import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.data.NotePageDao

class NotePageRepository(private val notePageDao: NotePageDao, private val noteId: Long) {
    val getNotePage: LiveData<List<NotePage>> = notePageDao.getAll(noteId)

    fun insert(page: NotePage) {
        notePageDao.insert(page)
    }

    fun getNotePageItem(id: Long) = notePageDao.getItem(id, noteId)
}