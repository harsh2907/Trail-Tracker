package com.example.trailtracker.mainScreen.presentation.screens.statistics.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.domain.models.RunItem
import com.example.trailtracker.mainScreen.domain.usecases.SortRunsUseCase
import com.example.trailtracker.mainScreen.presentation.screens.statistics.utils.GraphType
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel @Inject constructor(
    private val sortRunsUseCase: SortRunsUseCase
) : ViewModel() {


    // Sort type that can be updated via the UI
    private val _sortType = MutableStateFlow(SortType.DURATION)

    private val _selectedGraphType = MutableStateFlow(GraphType.OVERALL)
    val selectedGraphType = _selectedGraphType.asStateFlow()

    fun onGraphTypeChanges(graphType: GraphType) {
        _selectedGraphType.update { graphType }
    }

    // Flow for the overall graph
    val overallPointsForGraph: StateFlow<Map<LocalDate, Float>> = _sortType
        .flatMapLatest { sortType ->
            sortRunsUseCase(sortType).map { runs ->
                try {
                    runs.associate { run ->
                        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

                        val formattedDate = Constants.formatEpochToDateString(run.createdAt).let {
                            LocalDate.parse(it, dateFormatter)
                        }
                        val yValue = mapRunMetricToYValue(sortType, run)

                        formattedDate to yValue
                    }
                } catch (e: Exception) {
                    Log.e("StatsVM", e.message, e)
                    emptyMap()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    // Flow for the weekly graph (filtering runs from the past 7 days)
    val weeklyPointsForGraph: StateFlow<Map<LocalDate, Float>> = _sortType
        .flatMapLatest { sortType ->
            sortRunsUseCase(sortType).map { runs ->
                try {
                    // Filter for runs in the past 7 days
                    val currentTime = System.currentTimeMillis()
                    val sevenDaysAgo =
                        currentTime - (7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds

                    runs.filter { run -> run.createdAt >= sevenDaysAgo }
                        .associate { run ->
                            val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

                            val formattedDate =
                                Constants.formatEpochToDateString(run.createdAt).let {
                                    LocalDate.parse(it, dateFormatter)
                                }
                            val yValue = mapRunMetricToYValue(sortType, run)

                            formattedDate to yValue
                        }
                } catch (e: Exception) {
                    Log.e("StatsVM", e.message, e)
                    emptyMap()
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val todayPointsForGraph: StateFlow<Map<Long, Float>> = _sortType
        .flatMapLatest { sortType ->
            sortRunsUseCase(sortType).map { runs ->
                try {
                    // Get the start and end of today in milliseconds
                    val todayStart = LocalDate.now()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    val todayEnd = LocalDate.now()
                        .plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    // Filter for runs created today
                    runs.filter { run ->
                        run.createdAt in todayStart until todayEnd
                    }.associate { run ->
//                        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

//                        val formattedDate =
//                            Constants.formatEpochToDateString(run.createdAt).let {
//                                LocalDate.parse(it, dateFormatter)
//                            }

                        val yValue = mapRunMetricToYValue(sortType, run)

                        run.createdAt to yValue
                    }
                } catch (e: Exception) {
                    Log.e("StatsVM", e.message, e)
                    emptyMap()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


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
        _sortType.update { sortType }
    }
}
