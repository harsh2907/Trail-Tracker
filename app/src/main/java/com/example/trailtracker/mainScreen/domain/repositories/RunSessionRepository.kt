package com.example.trailtracker.mainScreen.domain.repositories

import com.example.trailtracker.mainScreen.domain.models.RunEntity
import kotlinx.coroutines.flow.Flow

interface RunSessionRepository {
    suspend fun upsertRun(run: RunEntity)
    suspend fun deleteRun(run: RunEntity)
    suspend fun getUnsyncedRuns(): List<RunEntity>
    suspend fun deleteAllRuns()
    suspend fun getRunById(id: Long): RunEntity?
    fun getAllRunsSortedByDate(): Flow<List<RunEntity>>
    fun getAllRunsSortedBySpeed(): Flow<List<RunEntity>>
    fun getAllRunsSortedByDistance(): Flow<List<RunEntity>>
    fun getAllRunsSortedByDuration(): Flow<List<RunEntity>>
    fun getAllRunsSortedByCalories(): Flow<List<RunEntity>>
    fun getTotalDurationForSessions(): Flow<Long>
    fun getTotalCaloriesBurnedForSessions(): Flow<Int>
    fun getTotalDistanceCoveredForSessions(): Flow<Double>
    fun getTotalAverageSpeedForSessions(): Flow<Double>
}

