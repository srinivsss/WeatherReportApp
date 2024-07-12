package com.chase.android.weatherapplication.viewmodel

import PreferencesManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.android.weatherapplication.ApiResult
import com.chase.android.weatherapplication.ERROR_IN_CITYNAME
import com.chase.android.weatherapplication.GENERAL_ERROR
import com.chase.android.weatherapplication.R
import com.chase.android.weatherapplication.data.CurrentWeather
import com.chase.android.weatherapplication.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    lateinit var sharedPreferences: SharedPreferences
    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather = _currentWeather.asStateFlow()

    private val _errorMessage = mutableStateOf<ApiResult>(ApiResult.Success(""))

    /**
     * Get Current Weather based on cityName
     */
    @VisibleForTesting
    fun getCurrentWeatherBasedOnCity(cityName: String?) {
        viewModelScope.launch {
            cityName?.let {
                PreferencesManager(sharedPreferences).saveData("CITY_NAME",cityName)
                val currentWeather = repository.getCurrentWeatherBasedOnCity(it)
                currentWeather?.let { weather ->
                    _currentWeather.value = weather
                }?:kotlin.run {
                    _errorMessage.value = ApiResult.Error(GENERAL_ERROR)
                }
            }?:kotlin.run {
                _errorMessage.value = ApiResult.Error(ERROR_IN_CITYNAME)
            }
        }
    }


}