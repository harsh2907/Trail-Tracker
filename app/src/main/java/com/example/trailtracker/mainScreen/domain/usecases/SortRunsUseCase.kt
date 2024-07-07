package com.example.trailtracker.mainScreen.domain.usecases

import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.repositories.RunRepository
import com.example.trailtracker.utils.SortType
import kotlinx.coroutines.flow.Flow

class SortRunsUseCase(
    private val runRepository: RunRepository
) {

    operator fun invoke(sortType: SortType): Flow<List<Run>> {
        return when (sortType) {
            SortType.DATE -> runRepository.getAllRunsSortedByDate()
            SortType.SPEED -> runRepository.getAllRunsSortedBySpeed()
            SortType.DISTANCE -> runRepository.getAllRunsSortedByDistance()
            SortType.DURATION -> runRepository.getAllRunsSortedByDuration()
            SortType.CALORIES -> runRepository.getAllRunsSortedByCalories()
        }
    }
}