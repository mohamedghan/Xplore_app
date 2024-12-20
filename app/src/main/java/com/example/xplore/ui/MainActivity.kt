package com.example.xplore.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.xplore.data.LocationService
import com.example.xplore.data.TokenManager
import com.example.xplore.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var journeyLocator: JourneyGlobal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
              TravelNavGraph(tokenManager=tokenManager, locationService = locationService, journeyLocator = journeyLocator)
            }
        }
    }
}