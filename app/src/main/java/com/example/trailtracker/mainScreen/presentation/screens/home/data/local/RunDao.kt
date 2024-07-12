package com.example.trailtracker.mainScreen.presentation.screens.home.data.local

import androidx.room.*
import com.example.trailtracker.mainScreen.domain.models.Run
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("DELETE FROM runningSession_table")
    suspend fun deleteAllRuns()

    @Query("SELECT * FROM runningSession_table ORDER BY createdAt DESC")
    fun getAllRunsSortedByDate(): Flow<List<Run>>

    @Query("SELECT * FROM runningSession_table ORDER BY averageSpeedInKPH DESC")
    fun getAllRunsSortedBySpeed(): Flow<List<Run>>

    @Query("SELECT * FROM runningSession_table ORDER BY distanceCovered DESC")
    fun getAllRunsSortedByDistance(): Flow<List<Run>>

    @Query("SELECT * FROM runningSession_table ORDER BY sessionDuration DESC")
    fun getAllRunsSortedByDuration(): Flow<List<Run>>

    @Query("SELECT * FROM runningSession_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCalories(): Flow<List<Run>>

    @Query("SELECT * FROM runningSession_table WHERE id = :id")
    suspend fun getRunById(id: Long): Run?

    @Query("SELECT SUM(sessionDuration) FROM runningSession_table")
    fun getTotalDurationForSessions():Flow<Long>

    @Query("SELECT SUM(caloriesBurned) FROM runningSession_table")
    fun getTotalCaloriesBurnedForSessions():Flow<Int>

    @Query("SELECT SUM(distanceCovered) FROM runningSession_table")
    fun getTotalDistanceCoveredForSessions():Flow<Double>

    @Query("SELECT SUM(averageSpeedInKPH) FROM runningSession_table")
    fun getTotalAverageSpeedForSessions():Flow<Double>

}
