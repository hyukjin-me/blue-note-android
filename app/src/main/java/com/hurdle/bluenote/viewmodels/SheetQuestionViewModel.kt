package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.repository.SheetQuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SheetQuestionViewModel(application: Application, sheetId: Long) : AndroidViewModel(application) {

    private val repository: SheetQuestionRepository

    val questions: LiveData<List<Question>>

    init {
        val questionDao = NoteDatabase.getDatabase(application).questionDao
        repository = SheetQuestionRepository(questionDao, sheetId)
        questions = repository.questions
    }

    fun insert(questions: List<Question>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(questions)
    }

    fun update(question: Question)= viewModelScope.launch(Dispatchers.IO) {
        repository.update(question)
    }
}