package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.repository.NotePageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotePageViewModel(application: Application, noteId: Long) : AndroidViewModel(application) {

    private val repository: NotePageRepository

    val notePage: LiveData<List<NotePage>>

    private val _pageItem = MutableLiveData<NotePage>()
    val pageItem = _pageItem

    private val _navigateNoteOpen = MutableLiveData<NotePage?>()
    val navigateNoteOpen = _navigateNoteOpen

    private val _isEditState = MutableLiveData<Boolean>(false)
    val isEditState = _isEditState

    init {
        val notePageDao = NoteDatabase.getDatabase(application).notePageDao
        repository = NotePageRepository(notePageDao, noteId)
        notePage = repository.getNotePage
    }

    fun insert(notePage: NotePage) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notePage)
    }

    fun getNotePageItem(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val notePageItem = repository.getNotePageItem(id)
        _pageItem.postValue(notePageItem)
    }

    fun deleteNotePageItem(id: Long, noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotePageItem(id, noteId)
    }

    fun changeEditState(edit: Boolean) {
        _isEditState.value = edit
    }

    fun navigateNoteOpen(notePage: NotePage) {
        _navigateNoteOpen.value = notePage
    }

    fun doneNavigateNotePage() {
        _isEditState.value = false
        _navigateNoteOpen.value = null
    }
}