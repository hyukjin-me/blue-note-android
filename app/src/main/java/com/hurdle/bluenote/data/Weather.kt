package com.hurdle.bluenote.data

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

data class WeatherSys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

data class WeatherMain(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Int,
    val temp_min: Int
)

data class WeatherCoord(
    val lat: Double,
    val lon: Double
)

data class WeatherClouds(
    val all: Int
)

data class WeatherList(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class WeatherWind(
    val deg: Int,
    val speed: Double
)