package com.example.trailtracker.mainScreen.presentation.screens.home.data.repository

import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDao
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.repositories.RunRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunRepositoryImpl @Inject constructor(
    private val runDao: RunDao
) : RunRepository {

    override suspend fun upsertRun(run: Run) {
        runDao.upsertRun(run)
    }


    override suspend fun deleteRun(run: Run) {
        runDao.deleteRun(run)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }

    override fun getAllRunsSortedByDate(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDate()
    }

    override fun getAllRunsSortedBySpeed(): Flow<List<Run>> {
        return runDao.getAllRunsSortedBySpeed()
    }

    override fun getAllRunsSortedByDistance(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDistance()
    }

    override fun getAllRunsSortedByDuration(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDuration()
    }

    override fun getAllRunsSortedByCalories(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByCalories()
    }

    override suspend fun getRunById(id: Long): Run? {
        return runDao.getRunById(id)
    }

    override fun getTotalDurationForSessions(): Flow<Long> {
        return runDao.getTotalDurationForSessions()
    }

    override fun getTotalCaloriesBurnedForSessions(): Flow<Int> {
        return runDao.getTotalCaloriesBurnedForSessions()
    }

    override fun getTotalDistanceCoveredForSessions(): Flow<Double> {
        return runDao.getTotalDistanceCoveredForSessions()
    }

    override fun getTotalAverageSpeedForSessions(): Flow<Double> {
        return runDao.getTotalAverageSpeedForSessions()
    }
}
