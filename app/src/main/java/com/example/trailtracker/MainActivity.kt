package com.example.trailtracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.mainScreen.services.TrackingService
import com.example.trailtracker.navigation.TrailTrackerNavigation
import com.example.trailtracker.onboarding.AuthenticationViewModel
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.TrackingUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

                    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
                    val startDestination by authenticationViewModel.startDestination.collectAsStateWithLifecycle()

                    val window = (LocalView.current.context as Activity).window
                    LaunchedEffect(Unit) {
                        window.statusBarColor = Color.Black.toArgb()
                    }

                    LaunchedEffect(intent) {
                        handleIntent(intent)
                    }



                    TrailTrackerNavigation(
                        navController = navController,
                        startDestination = startDestination
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
        } else {
            navigateToSession.update { false }
        }
    }

    companion object {
        val navigateToSession = MutableStateFlow(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        TrackingUtils.sendCommandToService(this,Constants.STOP_SERVICE)
    }
}


