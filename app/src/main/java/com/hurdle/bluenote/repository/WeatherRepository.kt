package com.hurdle.bluenote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.hurdle.bluenote.data.WeatherCache
import com.hurdle.bluenote.data.WeatherDao

class WeatherRepository(private val weatherDao: WeatherDao) {

    val getWeather: LiveData<WeatherCache> = weatherDao.get()

    @WorkerThread
    fun insert(weather:WeatherCache){
        weatherDao.insert(weather)
    }
}