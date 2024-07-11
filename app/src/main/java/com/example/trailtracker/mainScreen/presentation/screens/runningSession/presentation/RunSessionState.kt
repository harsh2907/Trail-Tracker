package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import android.location.Location
import com.example.trailtracker.mainScreen.services.ColoredPolyline
import com.example.trailtracker.mainScreen.services.Polylines
import com.google.android.gms.maps.model.LatLng

data class RunSessionState(
    val polylinePoints: MutableList<ColoredPolyline> = mutableListOf(),
    val cameraPosition: LatLng = LatLng(0.0, 0.0),
    val speedInKph: Double = 0.0,
    val distanceCoveredInMeters: Double = 0.0,
    val averageSpeedInKph:Double = 0.0,
    val isTracking: Boolean = false,
    val sessionDuration:Long = 0L
)
