package com.example.trailtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.trailtracker.mainScreen.services.TrackingService
import com.example.trailtracker.navigation.Screens
import com.example.trailtracker.navigation.TrailTrackerNavigation
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.TrackingUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrailTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(intent) {
                        handleIntent(intent)
                    }

                    val startDestination =  if (Firebase.auth.uid == null) Screens.AuthScreen.route else Screens.MainScreen.route
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


