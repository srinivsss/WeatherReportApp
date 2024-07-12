package com.chase.android.weatherapplication

import PreferencesManager
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chase.android.weatherapplication.data.CurrentWeather
import com.chase.android.weatherapplication.theme.LightBlue
import com.chase.android.weatherapplication.theme.RainGray
import com.chase.android.weatherapplication.theme.SunnyGreen
import com.chase.android.weatherapplication.theme.WeatherAppTheme
import com.chase.android.weatherapplication.viewmodel.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    private val viewModel: HomeScreenViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setContent {
            WeatherAppTheme {
                val permission =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

                PermissionRequired(
                    permissionState = permission,
                    permissionNotGrantedContent = { LocationPermissionDetails(onContinueClick = permission::launchPermissionRequest) },
                    permissionNotAvailableContent = { LocationPermissionNotAvailable(onContinueClick = permission::launchPermissionRequest) }
                ) {
                    HomeScreen(viewModel)
                }
            }
        }
        val cityName = PreferencesManager(viewModel.sharedPreferences).getData("CITY_NAME", "")
        if (!cityName.isNullOrEmpty()) {
            viewModel.getCurrentWeatherBasedOnCity(cityName)
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel) {
    val current = viewModel.currentWeather.collectAsState()
    current.value?.let {
        rememberSystemUiController().setStatusBarColor(it.backgroundColour())
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(current.value?.backgroundColour() ?: Color.White),
        verticalArrangement = Arrangement.Center
    ) {
        CityInput()
        current.value?.let {
            WeatherSummary(weather = it)
            Spacer(modifier = Modifier.height(30.dp))
            TemperatureSummary(it)
            Divider(color = Color.White)
        }
    }
}

@Composable
fun CityInput(homeScreenViewModel: HomeScreenViewModel = viewModel()) {
    Column(modifier = Modifier.wrapContentSize(), verticalArrangement = Arrangement.Top) {
        var cityName by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            value = cityName,
            singleLine = true,
            shape = shapes.large,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { cityName = it },
            label = { Text(stringResource(R.string.enter_city_name)) },

            )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                homeScreenViewModel.getCurrentWeatherBasedOnCity(cityName)
            }, enabled = !cityName.isNullOrEmpty()
        ) {
            Text(
                text = stringResource(R.string.get_weather),
                fontSize = 16.sp
            )
        }

    }
}

@Composable
fun WeatherSummary(weather: CurrentWeather) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val iconName = weather.weather.first().icon
        iconName?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://openweathermap.org/img/wn/$it.png")
                    .crossfade(true)
                    .build(), contentDescription = "Weather",
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .height(50.dp)
                    .width(50.dp)
            )
        }
        Text(
            text = formatTemperature(weather.main.temp),
            fontSize = 48.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        weather.weather.first().main?.let {
            Text(
                text = it,
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = weather.name,
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TemperatureSummary(weather: CurrentWeather) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTemperature(weather.main.tempMin),
                fontSize = 18.sp,
                color = Color.White
            )
            Text(text = stringResource(R.string.min_temperature), color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTemperature(weather.main.temp),
                fontSize = 18.sp,
                color = Color.White
            )
            Text(text = stringResource(R.string.now_temperature), color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTemperature(weather.main.tempMax),
                fontSize = 18.sp,
                color = Color.White
            )
            Text(text = stringResource(R.string.max_temperature), color = Color.White)
        }
    }
}

@Composable
private fun formatTemperature(temperature: Double): String {
    return stringResource(R.string.temperature_degrees, temperature.roundToInt())
}

private fun CurrentWeather.backgroundColour(): Color {
    val conditions = weather.first().main
    return when {
        conditions?.contains("cloud", ignoreCase = true) == true -> LightBlue
        conditions?.contains("rain", ignoreCase = true) == true -> RainGray
        else -> SunnyGreen
    }
}



