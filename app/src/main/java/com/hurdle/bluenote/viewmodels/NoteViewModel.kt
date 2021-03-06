package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    val notes: LiveData<List<Note>>
    val recentNotes: LiveData<List<Note>>

    private val _note = MutableLiveData<Note?>()
    val note = _note

    private val _searchNote = MutableLiveData<List<Note>?>()
    val searchNote = _searchNote

    private val _navigateToEdit = MutableLiveData<Note?>()
    val navigateToEdit = _navigateToEdit

    private val _navigateToPage = MutableLiveData<Note?>()
    val navigateToPage = _navigateToPage

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao
        repository = NoteRepository(noteDao)
        notes = repository.getNotes
        recentNotes = repository.getRecentNotes
    }

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun deleteNotePages(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotePage(id)
    }

    fun searchNote(text: String) = viewModelScope.launch(Dispatchers.IO) {
        val searchNote: List<Note> = repository.searchNote(text)
        _searchNote.postValue(searchNote)
    }

    fun getNote(noteId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val getNote = repository.getNote(noteId)
        _note.postValue(getNote)
    }

    // 편집모드로 이동
    fun navigateToEdit(note: Note) {
        _navigateToEdit.value = note
    }

    // 편집모드로 이동 완료
    fun doneNavigateEdit() {
        _navigateToEdit.value = null
    }

    fun navigateToPage(note: Note) {
        _navigateToPage.value = note
    }

    fun doneNavigatePage() {
        _navigateToPage.value = null
    }
}