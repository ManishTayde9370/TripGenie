package com.example.tripgenie.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo
)
data class WeatherInfo(val main: String)
data class MainInfo(val temp: Double)

interface ApiService {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>
}