package com.hurdle.bluenote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.data.SheetDao

class SheetRepository(private val sheetDao: SheetDao) {

    val sheets: LiveData<List<Sheet>> = sheetDao.getAll()

    @WorkerThread
    fun insert(sheet: Sheet) {
        sheetDao.insert(sheet)
    }
}