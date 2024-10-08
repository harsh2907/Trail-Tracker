package com.example.trailtracker.mainScreen.domain.usecases

import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.models.RunItem
import com.example.trailtracker.utils.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SortRunsUseCase(
    private val firebaseRunRepository: FirebaseRunRepository
) {

    operator fun invoke(sortType: SortType): Flow<List<RunItem>> {
        val allRuns = firebaseRunRepository.runsFlow.map { it.map { run -> run.toRunItem() } }
        return when (sortType) {
            SortType.DATE -> allRuns.map { run -> run.sortedByDescending { it.createdAt } }
            SortType.SPEED -> allRuns.map { run -> run.sortedByDescending { it.averageSpeedInKPH } }
            SortType.DISTANCE -> allRuns.map { run -> run.sortedByDescending { it.distanceCoveredInMeters } }
            SortType.DURATION -> allRuns.map { run -> run.sortedByDescending { it.sessionDurationInSeconds } }
            SortType.CALORIES -> allRuns.map { run -> run.sortedByDescending { it.caloriesBurned } }
        }
    }
    /*    operator fun invoke(sortType: SortType): Flow<Result<List<Run>>> {
            return when (sortType) {
                SortType.DATE -> firebaseRunRepository.getAllRunsSortedByDate()
                SortType.SPEED -> firebaseRunRepository.getAllRunsSortedBySpeed()
                SortType.DISTANCE -> firebaseRunRepository.getAllRunsSortedByDistance()
                SortType.DURATION -> firebaseRunRepository.getAllRunsSortedByDuration()
                SortType.CALORIES -> firebaseRunRepository.getAllRunsSortedByCalories()
            }
        }*/
}