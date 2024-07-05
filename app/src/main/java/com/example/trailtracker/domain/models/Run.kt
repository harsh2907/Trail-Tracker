package com.example.trailtracker.domain.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.trailtracker.data.local.BitmapConverter

@Entity("runningSession_table")
@TypeConverters(BitmapConverter::class)
data class Run(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val image:Bitmap? = null,
    val createdAt:Long = System.currentTimeMillis(),
    val sessionDuration:Long = 0L,
    val averageSpeedInKPH:Double = 0.0,
    val distanceCovered:Double = 0.0,
    val caloriesBurned:Int = 0
)
