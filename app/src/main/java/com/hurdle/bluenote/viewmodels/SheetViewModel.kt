package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.repository.SheetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SheetRepository

    val sheets: LiveData<List<Sheet>>
    val recentSheets: LiveData<List<Sheet>>

    private val _searchSheet = MutableLiveData<List<Sheet>?>()
    val searchSheet = _searchSheet

    private val _navigateToEdit = MutableLiveData<Sheet?>()
    val navigateToEdit = _navigateToEdit

    private val _navigateToQuestion = MutableLiveData<Sheet?>()
    val navigateToQuestion = _navigateToQuestion

    private val _sheet = MutableLiveData<Sheet?>()
    val sheet = _sheet

    init {
        val sheetDao = NoteDatabase.getDatabase(application).sheetDao
        repository = SheetRepository(sheetDao)
        sheets = repository.sheets
        recentSheets = repository.recentSheets
    }

    fun insert(sheet: Sheet) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sheet)
    }

    fun deleteSheet(sheet: Sheet) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSheet(sheet)
    }

    fun deleteSheetQuestions(sheetId: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSheetQuestions(sheetId)
    }

    fun searchSheet(text: String) = viewModelScope.launch(Dispatchers.IO) {
        val searchSheet: List<Sheet> = repository.searchSheet(text)
        _searchSheet.postValue(searchSheet)
    }

    fun getSheet(sheetId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val getNote = repository.getSheet(sheetId)
        _sheet.postValue(getNote)
    }

    fun updateSheet(sheet: Sheet?) = viewModelScope.launch(Dispatchers.IO) {
        if (sheet != null) {
            repository.update(sheet)
        }
    }

    fun navigateToEdit(sheet: Sheet) {
        _navigateToEdit.value = sheet
    }

    fun doneNavigateEdit() {
        _navigateToEdit.value = null
    }

    fun navigateToQuestion(sheet: Sheet) {
        _navigateToQuestion.value = sheet
    }

    fun doneNavigateQuestion() {
        _navigateToQuestion.value = null
    }
}