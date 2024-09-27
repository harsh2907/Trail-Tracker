package com.example.trailtracker.mainScreen.domain.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "runningSession_table")
data class RunEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val imageBitmap: Bitmap, // Bitmap stored as ByteArray
    val createdAt: Long = System.currentTimeMillis(),
    val sessionDuration: Long = 0L,
    val averageSpeedInKPH: Double = 0.0,
    val distanceCoveredInMeters: Double = 0.0,
    val caloriesBurned: Int = 0,
    val isSynced: Boolean = false // Flag to track if the run is synced to Firebase
){
    fun toRun():Run{
        return Run(
            id = this.id,
            createdAt = this.createdAt,
            sessionDuration = this.sessionDuration,
            averageSpeedInKPH = this.averageSpeedInKPH,
            distanceCoveredInMeters = this.distanceCoveredInMeters,
            caloriesBurned = this.caloriesBurned
        )
    }

    fun toRunItem():RunItem{
        return RunItem(
            id = this.id,
            imageBitmap = this.imageBitmap,
            createdAt = this.createdAt,
            sessionDurationInSeconds = this.sessionDuration,
            averageSpeedInKPH = this.averageSpeedInKPH,
            distanceCoveredInMeters = this.distanceCoveredInMeters,
            caloriesBurned = this.caloriesBurned
        )
    }
}
