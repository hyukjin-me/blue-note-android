package com.hurdle.bluenote.data

import androidx.annotation.Keep

// @Keep
// https://stackoverflow.com/questions/62770065/after-building-release-version-retrofit2-gets-200-but-empty-data
@Keep
data class Weather(
    val id: Int,
    val base: String,
    val cod: Int,
    val dt: Int,
    val timezone: Int,
    val visibility: Int,
    val name: String,
    val clouds: WeatherClouds,
    val coord: WeatherCoord,
    val main: WeatherMain,
    val sys: WeatherSys,
    val weather: List<WeatherList>,
    val wind: WeatherWind
)

@Keep
data class WeatherSys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

@Keep
data class WeatherMain(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Int,
    val temp_min: Int
)

@Keep
data class WeatherCoord(
    val lat: Double,
    val lon: Double
)

@Keep
data class WeatherClouds(
    val all: Int
)

@Keep
data class WeatherList(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

@Keep
data class WeatherWind(
    val deg: Int,
    val speed: Double
)