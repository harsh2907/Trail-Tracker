package com.example.trailtracker.mainScreen.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.mainScreen.presentation.Destinations
import com.example.trailtracker.mainScreen.presentation.screens.home.presentation.HomeScreen
import com.example.trailtracker.mainScreen.presentation.screens.home.presentation.HomeViewModel
import com.example.trailtracker.mainScreen.presentation.screens.profile.presentation.ProfileScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.RunningSessionScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.RunningSessionViewModel
import com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation.StatisticsScreen
import com.example.trailtracker.mainScreen.services.TrackingService
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.MapStyle
import com.example.trailtracker.utils.TrackingUtils
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreenNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destinations.Home.route
    ) {

        composable(route = Destinations.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()

            val sortType by homeViewModel.sortType.collectAsStateWithLifecycle()
            val allRuns by homeViewModel.allRuns.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val activity = context as Activity



            HomeScreen(
                allRuns = allRuns,
                sortType = sortType,
                navigateToSession = {
                    TrackingService.resetStates()
                    navController.navigate(Destinations.Home.Run.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Destinations.Statistics.route) {
            StatisticsScreen()
        }

        composable(route = Destinations.Profile.route) {
            ProfileScreen()
        }

        runNavigation(
            navigateToHome = {
                navController.navigateUp()
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraphBuilder.runNavigation(
    navigateToHome: () -> Unit
) {
    composable(route = Destinations.Home.Run.route) {

        val runningSessionViewModel: RunningSessionViewModel = viewModel()
        val runSessionState by runningSessionViewModel.runSessionState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        fun sendCommandToService(action: String) {
            val intent = Intent(context, TrackingService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }


        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                runSessionState.cameraPosition,
                16f
            )
        }

        LaunchedEffect(runSessionState.cameraPosition) {
            if (!cameraPositionState.isMoving) {
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(
                        runSessionState.cameraPosition,
                        cameraPositionState.position.zoom
                    )
            }
        }

        val uiSettings = remember {
            MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true
            )
        }

        val mapProperties = remember {
            MapProperties(
                mapStyleOptions = MapStyleOptions(MapStyle.json),
                minZoomPreference = 5f
            )
        }

        BackHandler {
            sendCommandToService(Constants.STOP_SERVICE)
            navigateToHome()
        }


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (runSessionState.isTracking) {
                            sendCommandToService(Constants.PAUSE_SERVICE)
                        } else {
                            sendCommandToService(Constants.START_OR_RESUME_SERVICE)
                        }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (runSessionState.isTracking) Icons.Rounded.Stop else Icons.Default.PlayArrow,
                        contentDescription = "run",
                        tint = Color.Black
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {
            RunningSessionScreen(
                state = runSessionState,
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                mapProperties = mapProperties
            )
        }

    }
}

