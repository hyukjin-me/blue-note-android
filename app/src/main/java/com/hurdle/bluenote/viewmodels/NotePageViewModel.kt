package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.repository.NotePageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotePageViewModel(application: Application, noteId: Long) : AndroidViewModel(application) {

    private val repository: NotePageRepository

    val notePage: LiveData<List<NotePage>>

    init {
        val notePageDao = NoteDatabase.getDatabase(application).notePageDao
        repository = NotePageRepository(notePageDao, noteId)
        notePage = repository.getNotePage
    }

    fun insert(notePage: NotePage) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notePage)
    }
}