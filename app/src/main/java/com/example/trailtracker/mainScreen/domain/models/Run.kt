package com.example.trailtracker.mainScreen.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("runningSession_table")
data class Run(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sessionDuration: Long = 0L,
    val averageSpeedInKPH: Double = 0.0,
    val distanceCovered: Double = 0.0,
    val caloriesBurned: Int = 0
)
