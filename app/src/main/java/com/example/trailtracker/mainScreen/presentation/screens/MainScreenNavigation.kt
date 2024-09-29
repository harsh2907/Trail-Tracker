package com.example.trailtracker.mainScreen.presentation.screens

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.domain.models.RunEntity
import com.example.trailtracker.mainScreen.presentation.Destinations
import com.example.trailtracker.mainScreen.presentation.screens.home.presentation.HomeScreen
import com.example.trailtracker.mainScreen.presentation.screens.home.presentation.HomeViewModel
import com.example.trailtracker.mainScreen.presentation.screens.profile.ProfileViewModel
import com.example.trailtracker.mainScreen.presentation.screens.profile.presentation.ProfileScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.EndSessionDialog
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.RunningSessionScreen
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.RunningSessionViewModel
import com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation.DemoChart
import com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation.StatisticsViewModel
import com.example.trailtracker.navigation.Screens
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.MapStyle
import com.example.trailtracker.utils.SortType
import com.example.trailtracker.utils.TrackingUtils
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.time.LocalDate

@Composable
fun MainScreenNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigateToAuthScreen: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destinations.Home.route
    ) {

        composable(route = Destinations.Home.route) { backstack ->
            val parentEntry = remember(backstack) {
                navController.getBackStackEntry(Destinations.Home.route)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
            val context = LocalContext.current

            val state by homeViewModel.allRunsState.collectAsStateWithLifecycle()
            val currentUser by homeViewModel.currentUser.collectAsStateWithLifecycle()

            LaunchedEffect(state.error) {
                if (state.error.isNotEmpty()) {
                    Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                }
            }

            if (currentUser == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                currentUser?.let { user ->
                    HomeScreen(
                        state = state,
                        user = user,
                        onSortTypeChanged = homeViewModel::onSortTypeChanged,
                        onRefresh = homeViewModel::onRefresh,
                        navigateToSession = {
                            navController.navigate(Destinations.Home.Run.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onDeleteSession = { runItem ->
                            homeViewModel.deleteRun(
                                runItem = runItem,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Session deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onSignOut = {
                            homeViewModel.signOut(
                                onSuccess = navigateToAuthScreen,
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                }
            }
        }

        composable(route = Destinations.Statistics.route) {
            val statisticsViewModel: StatisticsViewModel = hiltViewModel()

            // Launch effect to set the initial sort type
            LaunchedEffect(Unit) {
                statisticsViewModel.updateSortType(sortType = SortType.DURATION)
            }

            // Collect overall points for the graph
            val overAllPoints by statisticsViewModel.overallPointsForGraph.collectAsStateWithLifecycle()

            if (overAllPoints.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Stats Screen")
                }
            } else {



                // Render the StatisticsScreen with updated line chart data
                DemoChart(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    overAllPoints = overAllPoints
                )
            }


        }

        composable(route = Destinations.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val currentUser by profileViewModel.currentUser.collectAsStateWithLifecycle()
            val totalDistance by profileViewModel.getTotalDistance()
                .collectAsStateWithLifecycle(initialValue = 0.0)
            val totalDuration by profileViewModel.getTotalTimeSpend()
                .collectAsStateWithLifecycle(initialValue = 0L)
            val averageSpeed by profileViewModel.getAverageSpeed()
                .collectAsStateWithLifecycle(initialValue = 0.0)
            val totalCalories by profileViewModel.getTotalCalories()
                .collectAsStateWithLifecycle(initialValue = 0)

            LaunchedEffect(key1 = currentUser) {
                if (currentUser == null) {
                    navController.popBackStack()
                    navController.navigate(Screens.AuthScreen.route)
                }
            }

            var selectedImageUri: Uri? by remember { mutableStateOf(null) }


            val imagePicker =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        if (uri != null) {
                            selectedImageUri = uri
                        }
                    })


            LaunchedEffect(key1 = selectedImageUri) {
                selectedImageUri?.let { uri: Uri ->
                    profileViewModel.updateUserProfileImage(imageUri = uri)
                }
            }

            ProfileScreen(
                currentUser = currentUser!!,
                totalDistance = totalDistance,
                totalDuration = totalDuration,
                averageSpeed = averageSpeed,
                totalCalories = totalCalories,
                selectedImageUri = selectedImageUri,
                onProfileImageClicked = {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
        }

        runNavigation(
            navController = navController,
            navigateToHome = {
                navController.navigateUp()
            }
        )
    }
}


fun NavGraphBuilder.runNavigation(
    navController: NavHostController,
    navigateToHome: () -> Unit
) {
    composable(route = Destinations.Home.Run.route) { backstack ->
        val parentEntry = remember(backstack) {
            navController.getBackStackEntry(Destinations.Home.route)
        }

        val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
        val runningSessionViewModel: RunningSessionViewModel = hiltViewModel()
        val state by runningSessionViewModel.runSessionState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        var isDialogVisible by remember { mutableStateOf(false) }
        var isRunFinished by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }


        val activity = context as Activity

        LaunchedEffect(Unit) {
            TrackingUtils.requestInitialLocation(activity)
        }


        AnimatedVisibility(visible = isDialogVisible) {

            EndSessionDialog(
                isLoading = isLoading,
                onResumeSession = {
                    isDialogVisible = false
                    TrackingUtils.sendCommandToService(context, Constants.START_OR_RESUME_SERVICE)
                },
                onFinishSession = {
                    isRunFinished = true
                }
            )
        }

        BackHandler {
            if (!state.isTracking && state.polylinePoints.isEmpty() && state.sessionDuration == 0L) {
                navigateToHome()
            } else {
                TrackingUtils.sendCommandToService(context, Constants.PAUSE_SERVICE)
                isDialogVisible = true
            }
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                state.cameraPosition,
                16f
            )
        }

        LaunchedEffect(state.cameraPosition) {
            if (!cameraPositionState.isMoving) {
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(
                        state.cameraPosition,
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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FloatingActionButton(
                        onClick = {
                            if (state.isTracking) {
                                TrackingUtils.sendCommandToService(context, Constants.PAUSE_SERVICE)
                            } else {
                                TrackingUtils.sendCommandToService(
                                    context,
                                    Constants.START_OR_RESUME_SERVICE
                                )
                            }
                        },
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (state.isTracking) Icons.Rounded.Stop else Icons.Default.PlayArrow,
                            contentDescription = "run",
                            tint = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    AnimatedVisibility(
                        visible = !state.isTracking,
                        enter = slideInHorizontally { -it } + fadeIn(),
                        exit = slideOutHorizontally { -it } + fadeOut()
                    ) {
                        FloatingActionButton(
                            onClick = {
                                TrackingUtils.sendCommandToService(context, Constants.STOP_SERVICE)
                                isDialogVisible = false
                                isLoading = false
                                navigateToHome()
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "cancel",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
            padding.calculateTopPadding()
            RunningSessionScreen(
                isRunFinished = isRunFinished,
                state = state,
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                mapProperties = mapProperties,
                isDialogVisible = isDialogVisible,
                onLoading = { isLoading = true },
                onSnapshot = { mapBitmap ->
                    if (mapBitmap == null) {
                        Toast.makeText(context, "No Progress to save", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Map", "Got our map bitmap here")
                        val runEntity = RunEntity(
                            imageBitmap = mapBitmap,
                            sessionDuration = state.sessionDuration,
                            averageSpeedInKPH = state.averageSpeedInKph,
                            distanceCoveredInMeters = state.distanceCoveredInMeters
                        )
                        homeViewModel.addRunItem(runEntity.toRunItem())
                        runningSessionViewModel.saveSessionToRoomDatabase(runEntity)
                    }

                    TrackingUtils.sendCommandToService(context, Constants.STOP_SERVICE)
                    isDialogVisible = false
                    isLoading = false
                    navigateToHome()
                }
            )
        }

    }
}

