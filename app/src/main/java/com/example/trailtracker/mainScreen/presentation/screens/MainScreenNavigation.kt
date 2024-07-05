package com.example.trailtracker.mainScreen.presentation.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.mainScreen.presentation.Destinations
import com.example.trailtracker.mainScreen.presentation.screens.home.presentation.HomeScreen
import com.example.trailtracker.mainScreen.presentation.screens.profile.presentation.ProfileScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.RunningSessionScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.RunningSessionViewModel
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.SessionStatus
import com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation.StatisticsScreen
import com.example.trailtracker.utils.MapStyle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
            HomeScreen(
                navigateToSession = {
                    navController.navigate(Destinations.Home.Run.route)
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

            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.runNavigation(
    navigateToHome: () -> Unit
) {
    composable(route = Destinations.Home.Run.route) {

        val runningSessionViewModel: RunningSessionViewModel = hiltViewModel()
        val runSessionState by runningSessionViewModel.runSessionState.collectAsStateWithLifecycle()

        val context = LocalContext.current

        val multiplePermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        fun hasLocationPermission(): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }


        LaunchedEffect(key1 = true) {
            if (hasLocationPermission()) {
                runningSessionViewModel.getLastLocation()
            } else {
                multiplePermissionState.launchMultiplePermissionRequest()
            }
        }

        LifecycleResumeEffect(key1 = true) {
            runningSessionViewModel.startLocationUpdates()

            onPauseOrDispose {
                runningSessionViewModel.stopLocationUpdates()
            }
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                runSessionState.cameraPosition ?: LatLng(0.0, 0.0),
                20f
            )
        }

        LaunchedEffect(runSessionState.cameraPosition) {
            runSessionState.cameraPosition?.let { loc ->
                if (!cameraPositionState.isMoving) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(loc, cameraPositionState.position.zoom)
                }
            }
        }

        val uiSettings = remember { MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = true
        ) }

        val mapProperties = remember {
            MapProperties(
                mapStyleOptions = MapStyleOptions(MapStyle.json),
                minZoomPreference = 5f,
                isMyLocationEnabled = true
            )
        }


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (runSessionState.sessionStatus != SessionStatus.STARTED) {
                            runningSessionViewModel.startSession()
                        } else {
                            runningSessionViewModel.pauseSession()
                        }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = when (runSessionState.sessionStatus) {
                            SessionStatus.STARTED -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
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

