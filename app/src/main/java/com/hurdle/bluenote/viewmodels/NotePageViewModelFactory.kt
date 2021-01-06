package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NotePageViewModelFactory(
    private val application: Application,
    private val id: Long
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotePageViewModel::class.java)) {
            return NotePageViewModel(application = application, id) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}