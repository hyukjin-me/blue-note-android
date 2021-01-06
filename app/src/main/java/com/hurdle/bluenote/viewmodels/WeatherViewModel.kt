package com.hurdle.bluenote.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hurdle.bluenote.BuildConfig
import com.hurdle.bluenote.data.NoteDatabase
import com.hurdle.bluenote.data.Weather
import com.hurdle.bluenote.data.WeatherCache
import com.hurdle.bluenote.remote.WeatherApi
import com.hurdle.bluenote.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository

    val currentWeather: LiveData<WeatherCache>

    init {
        val weatherDao = NoteDatabase.getDatabase(application).weatherDao
        repository = WeatherRepository(weatherDao)

        currentWeather = repository.getWeather
    }

    fun insert(weather: WeatherCache) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(weather)
    }

    fun getWeather() = viewModelScope.launch(Dispatchers.IO) {
        val weatherResponse: Response<Weather> =
            WeatherApi.weatherService.getWeather(appid = BuildConfig.WEATHER_KEY)

        // Weather API 가져오기 성공시 처리
        if (weatherResponse.isSuccessful) {
            val weather: Weather? = weatherResponse.body()
            if (weather != null) {

                val temperature: String = weather.main.temp.toString() // °C
                val atmosphere: String = weather.weather[0].description
                val location: String = weather.name

                val weatherCache = WeatherCache(
                    temperature = temperature,
                    atmosphere = atmosphere,
                    location = location
                )

                repository.insert(weatherCache)
            }
        }
    }
}