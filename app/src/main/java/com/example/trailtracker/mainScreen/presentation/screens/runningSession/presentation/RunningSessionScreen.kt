package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.formatTime
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import kotlin.math.roundToInt

@Composable
fun RunningSessionScreen(
    modifier: Modifier = Modifier,
    state: RunSessionState,
    cameraPositionState: CameraPositionState,
    uiSettings: MapUiSettings,
    mapProperties: MapProperties
) {

    val polylineColor by remember {
        derivedStateOf {
            if (state.speedInKph >= 12) {
                Color.Green
            } else if (state.speedInKph >= 6) {
                Color.Yellow
            } else {
                Color.Red
            }
        }
    }



    Box(modifier = modifier) {

        val currentLocationMarkerState = remember(state.cameraPosition) {
            MarkerState(
                position = state.cameraPosition
            )
        }

        LaunchedEffect(state) {
            Log.e("Points",state.polylinePoints.toString())
        }

        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties
        ) {

            CurrentLocationMarker(currentLocationMarkerState = currentLocationMarkerState)

            key(state){

                state.polylinePoints.forEach { polyline ->
                    Polyline(
                        points = polyline.points,
                        endCap = RoundCap(),
                        color = Color(polyline.color)
                    )
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
        state.sessionDuration.formatTime()
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