package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.domain.models.RunItem
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import com.example.trailtracker.mainScreen.domain.usecases.SortRunsUseCase
import com.example.trailtracker.mainScreen.presentation.screens.statistics.utils.DataPoint
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val sortRunsUseCase: SortRunsUseCase
) : ViewModel() {

    val currentUser = firebaseUserRepository.currentUser

    // Sort type that can be updated via the UI
    private val _sortType = MutableStateFlow(SortType.DURATION)

    // Flow for the overall graph
    val overallPointsForGraph: StateFlow<List<DataPoint>> = _sortType
        .flatMapLatest { sortType ->
            sortRunsUseCase(sortType).map { runs ->
                runs.map { run ->
                    // Mapping each RunItem to a Point for the overall graph
//                    val formattedDate = Constants.formatEpochToDateString(run.createdAt)
//                    val yValue = mapRunMetricToYValue(sortType, run)
//                    Point(x = run.createdAt.toFloat(), y = yValue)
// Assuming `minCreatedAt` is the earliest createdAt value (epoch) in your data
                    val minCreatedAt = runs.minOfOrNull { it.createdAt } ?: System.currentTimeMillis()
                    val addedAt = firebaseUserRepository.currentUser.value?.joinedAt ?: System.currentTimeMillis()
                    val xValue = ((run.createdAt - addedAt) / (1000 * 60 * 60 * 24)).toFloat()  // Days since `minCreatedAt`
                    val yValue = mapRunMetricToYValue(sortType, run)
                    DataPoint(x = xValue, y = yValue)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow for the weekly graph (filtering runs from the past 7 days)
    val weeklyPointsForGraph: StateFlow<List<DataPoint>> = _sortType
        .flatMapLatest { sortType ->
            sortRunsUseCase(sortType).map { runs ->
                // Filter for runs in the past 7 days
                val currentTime = System.currentTimeMillis()
                val sevenDaysAgo = currentTime - (7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds

                runs.filter { run -> run.createdAt >= sevenDaysAgo }
                    .map { run ->
                        val formattedDate = Constants.formatEpochToDateString(run.createdAt)
                        val yValue = mapRunMetricToYValue(sortType, run)
                        DataPoint(x = run.createdAt.toFloat(), y = yValue)
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helper function to map the selected metric to y-axis value
    private fun mapRunMetricToYValue(sortType: SortType, run: RunItem): Float {
        return when (sortType) {
            SortType.SPEED -> run.averageSpeedInKPH.toFloat()  // Speed in KPH
            SortType.DISTANCE -> run.distanceCoveredInMeters.toFloat()  // Distance in meters
            SortType.DURATION -> run.sessionDurationInSeconds.toFloat()  // Duration in minutes (from seconds)
            SortType.CALORIES -> run.caloriesBurned.toFloat()  // Calories burned
            SortType.DATE -> run.sessionDurationInSeconds.toFloat() // Default metric for DATE (Duration in minutes)
        }
    }


    // Function to update the sort type from UI
    fun updateSortType(sortType: SortType) {
        _sortType.value = sortType
    }
}
