package com.example.trailtracker.mainScreen.domain.models

import android.graphics.Bitmap

data class RunItem(
    val id: String,
    val imageUrl: String? = null,
    val imageBitmap: Bitmap? = null,
    val createdAt: Long,
    val sessionDurationInSeconds: Long,
    val averageSpeedInKPH: Double,
    val distanceCoveredInMeters: Double,
    val caloriesBurned: Int
)
