package com.chase.android.weatherapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chase.android.weatherapplication.theme.WeatherAppTheme
import com.chase.android.weatherapplication.viewmodel.HomeScreenViewModel
import org.junit.Rule
import org.junit.Test

class WeatherReportTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myTest() {
        composeTestRule.setContent {
            WeatherAppTheme(){
                CityInput()
            }
        }
        composeTestRule.onNodeWithText("Please Enter City Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Weather").performClick()
        composeTestRule.onNodeWithText("Cloudy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rainy").assertIsDisplayed()

    }
}