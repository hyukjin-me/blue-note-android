package com.hurdle.bluenote.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: WeatherCache)

    @Query("SELECT * FROM weather_cache ORDER BY id DESC LIMIT 1")
    fun get(): LiveData<WeatherCache>
}