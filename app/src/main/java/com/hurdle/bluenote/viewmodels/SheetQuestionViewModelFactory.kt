package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SheetQuestionViewModelFactory(
    private val application: Application,
    private val sheetId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SheetQuestionViewModel::class.java)) {
            return SheetQuestionViewModel(application, sheetId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}