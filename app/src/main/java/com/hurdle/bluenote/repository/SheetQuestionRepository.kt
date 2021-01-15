package com.hurdle.bluenote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.data.QuestionDao

class SheetQuestionRepository(private val questionDao: QuestionDao, sheetId: Long) {

    val questions: LiveData<List<Question>> = questionDao.getAll(sheetId)

    @WorkerThread
    fun insert(questions: List<Question>) {
        questionDao.insert(questions)
    }

    @WorkerThread
    fun update(question: Question){
        questionDao.update(question)
    }
}