package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SheetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sheet: Sheet)

    @Update
    fun update(sheet: Sheet)

    @Delete
    fun delete(sheet: Sheet)

    @Query("DELETE FROM question WHERE sheet_id = :sheetId")
    fun deleteSheetQuestions(sheetId: Long)

    @Query("SELECT * FROM sheet ORDER BY id DESC")
    fun getAll(): LiveData<List<Sheet>>

    @Query("SELECT * FROM sheet ORDER BY id DESC LIMIT 6")
    fun getRecentSheet(): LiveData<List<Sheet>>

    @Query("SELECT * FROM sheet WHERE id = :key")
    fun get(key: Long): Sheet

    @Query("SELECT * FROM sheet WHERE title LIKE :text")
    fun searchSheet(text: String): List<Sheet>
}