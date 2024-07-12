package com.chase.android.weatherapplication.repository

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.annotation.RequiresPermission
import com.chase.android.weatherapplication.BuildConfig
import com.chase.android.weatherapplication.data.CurrentWeather
import com.chase.android.weatherapplication.service.OpenWeatherService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: OpenWeatherService
) {
    @RequiresPermission(ACCESS_FINE_LOCATION)
    suspend fun getCurrentWeatherBasedOnCity(cityName: String): CurrentWeather? {
        val response  = service.getWeatherBasedOnCity(cityName, BuildConfig.API_KEY)
        if(response?.isSuccessful == true){
            return response.body()
        }
        return null
    }

}