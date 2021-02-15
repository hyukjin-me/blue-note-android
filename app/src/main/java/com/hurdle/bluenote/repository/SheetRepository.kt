package com.hurdle.bluenote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.data.SheetDao

class SheetRepository(private val sheetDao: SheetDao) {

    val sheets: LiveData<List<Sheet>> = sheetDao.getAll()
    val recentSheets: LiveData<List<Sheet>> = sheetDao.getRecentSheet()

    @WorkerThread
    fun insert(sheet: Sheet) {
        sheetDao.insert(sheet)
    }

    @WorkerThread
    fun deleteSheet(sheet: Sheet) {
        sheetDao.delete(sheet)
    }

    @WorkerThread
    fun deleteSheetQuestions(sheetId: Long) {
        sheetDao.deleteSheetQuestions(sheetId)
    }

    @WorkerThread
    fun searchSheet(text: String): List<Sheet> = sheetDao.searchSheet(text)

    @WorkerThread
    fun getSheet(sheetId: Long) = sheetDao.get(sheetId)

    @WorkerThread
    fun update(sheet: Sheet) {
        sheetDao.update(sheet)
    }
}