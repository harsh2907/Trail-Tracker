package com.example.trailtracker.mainScreen.presentation.screens.home.data.repository

import com.example.trailtracker.mainScreen.presentation.screens.home.data.local.RunDao
import com.example.trailtracker.mainScreen.domain.models.RunEntity
import com.example.trailtracker.mainScreen.domain.repositories.RunSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunSessionOfflineRepositoryImpl @Inject constructor(
    private val runDao: RunDao
) : RunSessionRepository {

    override suspend fun upsertRun(run: RunEntity) {
        runDao.upsertRun(run)
    }

    override suspend fun deleteRun(run: RunEntity) {
        runDao.deleteRun(run)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }

    override fun getAllRunsSortedByDate(): Flow<List<RunEntity>> {
        return runDao.getAllRunsSortedByDate()
    }

    override fun getAllRunsSortedBySpeed(): Flow<List<RunEntity>> {
        return runDao.getAllRunsSortedBySpeed()
    }

    override fun getAllRunsSortedByDistance(): Flow<List<RunEntity>> {
        return runDao.getAllRunsSortedByDistance()
    }

    override fun getAllRunsSortedByDuration(): Flow<List<RunEntity>> {
        return runDao.getAllRunsSortedByDuration()
    }

    override fun getAllRunsSortedByCalories(): Flow<List<RunEntity>> {
        return runDao.getAllRunsSortedByCalories()
    }

    override suspend fun getRunById(id: Long): RunEntity? {
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
