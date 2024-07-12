package com.example.trailtracker

import android.app.Activity
import android.content.Intent
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
import com.example.trailtracker.mainScreen.services.TrackingService
import com.example.trailtracker.navigation.TrailTrackerNavigation
import com.example.trailtracker.onboardingDetails.presentation.OnBoardingViewModel
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

                    LaunchedEffect(intent) {
                        handleIntent(intent)
                    }

                    val shouldNavigate by navigateToSession.collectAsStateWithLifecycle()


                    TrailTrackerNavigation(
                        navController = navController,
                        startDestination =startDestination,
                        shouldNavigateToSession = shouldNavigate
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Constants.ACTION_SHOW_TRACKING_SCREEN && TrackingService.isServiceActive) {
            navigateToSession.update { true }
        }else{
            navigateToSession.update { false }
        }
    }

    companion object{
        private val navigateToSession = MutableStateFlow(false)
    }
}


