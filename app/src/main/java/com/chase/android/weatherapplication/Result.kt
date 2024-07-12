package com.chase.android.weatherapplication

sealed class ApiResult {
    data class Success(val successMessage: String) : ApiResult()
    data class Error(val errorString: String?) : ApiResult()
}

const val GENERAL_ERROR = "Unable to get Weather Report. please try again"
const val ERROR_IN_CITYNAME = "Please enter city name"
