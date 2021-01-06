package com.hurdle.bluenote.remote


import com.hurdle.bluenote.data.Weather
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val BASE_URL = "https://api.openweathermap.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherService {
    @GET("data/2.5/weather/")
    suspend fun getWeather(
        // 위치
        @Query("q")
        q: String = "Seoul",

        // 섭씨
        @Query("units")
        units: String = "metric",

        // API 키
        @Query("appid")
        appid: String = ""
    ): Response<Weather>
}

object WeatherApi {
    val weatherService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}
