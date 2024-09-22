package com.example.trailtracker.mainScreen.domain.models

import java.util.UUID

data class Run(
    val id: String = UUID.randomUUID().toString(),
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sessionDuration: Long = 0L,
    val averageSpeedInKPH: Double = 0.0,
    val distanceCoveredInMeters: Double = 0.0,
    val caloriesBurned: Int = 0
)
