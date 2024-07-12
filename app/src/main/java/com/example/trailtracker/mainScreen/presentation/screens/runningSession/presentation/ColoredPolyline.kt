package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import com.google.android.gms.maps.model.LatLng

data class ColoredPolyline(
    val points: MutableList<LatLng>,
    val color: Int
)