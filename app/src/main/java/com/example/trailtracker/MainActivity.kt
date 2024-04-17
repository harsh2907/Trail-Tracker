package com.example.trailtracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.navigation.TrailTrackerNavigation
import com.example.trailtracker.onboardingDetails.presentation.OnBoardingViewModel
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //TrailTracker
            // - For those who love running off the beaten path and tracking their adventures.
            TrailTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val onBoardingViewModel: OnBoardingViewModel = hiltViewModel()
                    val startDestination by onBoardingViewModel.startDestination.collectAsStateWithLifecycle()

                    val window = (LocalView.current.context as Activity).window
                    LaunchedEffect(Unit) {
                        window.statusBarColor = Color.Black.toArgb()
                     //   WindowCompat.setDecorFitsSystemWindows(window, false)
                    }




                    TrailTrackerNavigation(navController = navController,startDestination =startDestination)
                }
            }
        }
    }
}


