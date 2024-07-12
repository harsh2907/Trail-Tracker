package com.example.trailtracker.mainScreen.domain.repositories

import com.example.trailtracker.mainScreen.domain.models.Run
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    suspend fun upsertRun(run: Run)
    suspend fun deleteRun(run: Run)
    suspend fun deleteAllRuns()
    suspend fun getRunById(id: Long): Run?
    fun getAllRunsSortedByDate(): Flow<List<Run>>
    fun getAllRunsSortedBySpeed(): Flow<List<Run>>
    fun getAllRunsSortedByDistance(): Flow<List<Run>>
    fun getAllRunsSortedByDuration(): Flow<List<Run>>
    fun getAllRunsSortedByCalories(): Flow<List<Run>>
    fun getTotalDurationForSessions(): Flow<Long>
    fun getTotalCaloriesBurnedForSessions(): Flow<Int>
    fun getTotalDistanceCoveredForSessions(): Flow<Double>
    fun getTotalAverageSpeedForSessions(): Flow<Double>
}

