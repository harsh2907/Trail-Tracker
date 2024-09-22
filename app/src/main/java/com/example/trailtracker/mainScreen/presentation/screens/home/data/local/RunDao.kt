package com.example.trailtracker.mainScreen.presentation.screens.home.data.local

import androidx.room.*
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.models.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("DELETE FROM runningSession_table")
    suspend fun deleteAllRuns()

    @Query("SELECT * FROM runningSession_table WHERE isSynced = 0")
    suspend fun getUnsyncedRuns(): List<RunEntity> // Only get unsynced runs

    @Query("SELECT * FROM runningSession_table ORDER BY createdAt DESC")
    fun getAllRunsSortedByDate(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runningSession_table ORDER BY averageSpeedInKPH DESC")
    fun getAllRunsSortedBySpeed(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runningSession_table ORDER BY distanceCoveredInMeters DESC")
    fun getAllRunsSortedByDistance(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runningSession_table ORDER BY sessionDuration DESC")
    fun getAllRunsSortedByDuration(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runningSession_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCalories(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runningSession_table WHERE id = :id")
    suspend fun getRunById(id: Long): RunEntity?

    @Query("SELECT SUM(sessionDuration) FROM runningSession_table")
    fun getTotalDurationForSessions():Flow<Long>

    @Query("SELECT SUM(caloriesBurned) FROM runningSession_table")
    fun getTotalCaloriesBurnedForSessions():Flow<Int>

    @Query("SELECT SUM(distanceCoveredInMeters) FROM runningSession_table")
    fun getTotalDistanceCoveredForSessions():Flow<Double>

    @Query("SELECT SUM(averageSpeedInKPH) FROM runningSession_table")
    fun getTotalAverageSpeedForSessions():Flow<Double>

}
