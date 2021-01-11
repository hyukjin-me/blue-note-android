package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.repository.SheetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SheetRepository

    val sheets: LiveData<List<Sheet>>

    init {
        val sheetDao = NoteDatabase.getDatabase(application).sheetDao
        repository = SheetRepository(sheetDao)
        sheets = repository.sheets
    }

    fun insert(sheet: Sheet) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sheet)
    }
}