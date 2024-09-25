package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.ktx.awaitSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun RunningSessionScreen(
    modifier: Modifier = Modifier,
    isRunFinished: Boolean,
    state: RunSessionState,
    isDialogVisible: Boolean,
    cameraPositionState: CameraPositionState,
    uiSettings: MapUiSettings,
    mapProperties: MapProperties,
    onLoading:()->Unit,
    onSnapshot: (Bitmap?) -> Unit
) {

    Box(modifier = modifier) {

        var triggerCapture by remember { mutableStateOf(false) }
        var isMapLoaded by remember { mutableStateOf(false) }
        val currentLocationMarkerState = remember { MarkerState(position = state.cameraPosition) }
        var createSnapshotJob: Job? = remember { null }
        val coroutineScope = rememberCoroutineScope()


        LaunchedEffect(state.cameraPosition, isRunFinished, isDialogVisible) {
            if (!isRunFinished && !isDialogVisible) {
                currentLocationMarkerState.position = state.cameraPosition
            }
        }

        /**
         Changing the modifier after run is finished so that it can adjust to the size of screenshot we want
         and also alpha 0 so that we can see the change ourselves
         **/
        GoogleMap(
            modifier = if (isRunFinished) {
                Modifier
                    .width(300.dp)
                    .aspectRatio(16 / 9f)
                    .alpha(0f)
                    .onSizeChanged {
                        if (it.width >= 300) {
                            triggerCapture = true
                        }
                    }
            } else Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapLoaded = {
                isMapLoaded = true
            }
        ) {
            if (!isRunFinished) {
                CurrentLocationMarker(currentLocationMarkerState = currentLocationMarkerState)
            }

            key(state) {

                state.polylinePoints.forEach { coloredPolyline ->
                    Polyline(
                        points = coloredPolyline.points,
                        endCap = RoundCap(),
                        color = Color(coloredPolyline.color)
                    )
                }
            }

            MapEffect(state, isRunFinished, triggerCapture, createSnapshotJob) { map ->
                if (isRunFinished && triggerCapture && createSnapshotJob == null) {
                    triggerCapture = false
                    onLoading()

                    val polyPoints = state.polylinePoints.map { it.points }.flatten()

                    if(polyPoints.isNotEmpty()){
                        val boundsBuilder = LatLngBounds.builder()
                        polyPoints.forEach {pos->
                            boundsBuilder.include(pos)
                        }

                        Log.e("CameraBounds", "Bounds: " + boundsBuilder.build().toString())

                        map.moveCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                boundsBuilder.build(),
                                100
                            )
                        )
                        map.setOnCameraIdleListener {
                            createSnapshotJob?.cancel()
                            createSnapshotJob = coroutineScope.launch {
                                // Make sure the map is sharp and focused before taking
                                // the screenshot
                                delay(500L)
                                map.awaitSnapshot()?.let(onSnapshot)
                            }
                        }
                    }else{
                        Log.e("Bound Builder","Empty poly-points")
                        onSnapshot(null)
                    }
                }
            }
        }
    }

    SessionDetailsCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        state = state
    )
}


@Composable
fun CurrentLocationMarker(
    currentLocationMarkerState: MarkerState
) {
    MarkerComposable(
        state = currentLocationMarkerState
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(UiColors.primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                contentDescription = "run_icon",
                tint = Color.Black,
                modifier = Modifier
                    .scale(1.2f)
                    .padding(8.dp)
            )
        }
    }

}

@Composable
fun SessionDetailsCard(
    modifier: Modifier = Modifier,
    state: RunSessionState
) {
    val sessionDuration = remember(state.sessionDuration) {
        Constants.formatTime(state.sessionDuration)
    }

    ElevatedCard(
        modifier = modifier,
        shape = ShapeDefaults.Medium,
        colors = CardDefaults.cardColors(
            containerColor = UiColors.EerieBlack,
            contentColor = Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Duration",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = sessionDuration,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                color = Color.White
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Pace",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )

                    Text(
                        text = "${state.speedInKph.roundToInt()} km/h",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Distance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )

                    Text(
                        text = "${state.distanceCoveredInMeters.roundToInt()} m",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Avg. Speed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )

                    Text(
                        text = "${state.averageSpeedInKph.roundToInt()} km/h",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SessionCard() {
    TrailTrackerTheme {
        SessionDetailsCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            state = RunSessionState()
        )
    }
}