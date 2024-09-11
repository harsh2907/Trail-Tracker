package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import com.example.trailtracker.mainScreen.services.ColoredPolylines
import com.google.android.gms.maps.model.LatLng

data class RunSessionState(
    val polylinePoints: ColoredPolylines = mutableListOf(),
    val cameraPosition: LatLng = LatLng(0.0, 0.0),
    val speedInKph: Double = 0.0,
    val distanceCoveredInMeters: Double = 0.0,
    val averageSpeedInKph:Double = 0.0,
    val isTracking: Boolean = false,
    val sessionDuration:Long = 0L
)
