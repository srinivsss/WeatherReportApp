package com.chase.android.weatherapplication.service

import com.chase.android.weatherapplication.data.CurrentWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("weather?units=metric")
    suspend fun getWeatherBasedOnCity(
        @Query("q") searchParam: String,
        @Query("appid") appid: String,
    ): Response<CurrentWeather>

}