package com.example.trailtracker.mainScreen.presentation.screens.runningSession

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class RunSessionState(
    val liveLocation: Location? = null,
    val markers: List<Location> = emptyList(),
    val cameraPosition: LatLng? = null,
    val speedInKph: Double = 0.0,
    val distanceCoveredInMeters: Double = 0.0,
    val averageSpeedInKph:Double = 0.0,
    val sessionStatus: SessionStatus = SessionStatus.IDLE,
    val sessionDuration:Long = 0L
)

enum class SessionStatus{
    STARTED,
    PAUSED,
    IDLE,
    END;
}