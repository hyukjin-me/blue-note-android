package com.hurdle.bluenote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val temperature: String = "",
    val location: String = "",
    val atmosphere: String = "",
    val time: Long = System.currentTimeMillis()
)