package com.example.trailtracker.mainScreen.presentation.screens.runningSession

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.data.LocationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningSessionViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationFlow = MutableStateFlow<Location?>(null)
    private val _markersFlow = MutableStateFlow<List<Location>>(emptyList())
    private val _cameraPositionFlow = MutableStateFlow<LatLng?>(null)
    private val _speedInKphFlow = MutableStateFlow<Double>(0.0)
    private val _distanceCoveredInMeters = MutableStateFlow(0.0)
    private val _speedArray = MutableStateFlow<List<Double>>(emptyList())
    private val _sessionStatus = MutableStateFlow(SessionStatus.IDLE)
    private val _sessionDuration = MutableStateFlow(0L)
    val lastLocation = locationRepository.lastLocation()

    val runSessionState = combine(
        _locationFlow,
        _markersFlow,
        _cameraPositionFlow,
        _speedInKphFlow,
        _distanceCoveredInMeters,
        _speedArray,
        _sessionStatus,
        _sessionDuration
    ) { locationFlow, markersFlow, cameraPositionFlow, speedInMpsFlow, distanceCoveredInMetersFlow, speedArray,sessionStatus,sessionDuration ->
        RunSessionState(
            liveLocation = locationFlow,
            markers = markersFlow,
            speedInKph = speedInMpsFlow,
            cameraPosition = cameraPositionFlow,
            distanceCoveredInMeters = distanceCoveredInMetersFlow,
            averageSpeedInKph = if (speedArray.isNotEmpty()) speedArray.average() else 0.0,
            sessionStatus = sessionStatus,
            sessionDuration = sessionDuration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RunSessionState())

    private var locationUpdateJob: Job? = null
    private var timerJob: Job? = null



    fun getLastLocation() {
        locationRepository.getLastLocation()
    }

    fun startSession() {
        _sessionStatus.update { SessionStatus.STARTED }
        startTimer()
    }

    fun pauseSession() {
        _sessionStatus.update { SessionStatus.PAUSED }
        pauseTimer()
    }

    fun endSession() {
        _sessionStatus.update { SessionStatus.END }
        stopTimer()
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _sessionDuration.update { it+1 }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun stopTimer() {
        _sessionDuration.update { 0 }
        timerJob?.cancel()
    }


    fun startLocationUpdates() {
        locationUpdateJob = viewModelScope.launch {
            locationRepository.startLocationUpdates()
                .collect { location ->
                    _locationFlow.update { location }
                    _cameraPositionFlow.update { LatLng(location.latitude, location.longitude) }

                    if (_sessionStatus.value == SessionStatus.STARTED) {
                        _markersFlow.update { it + location }

                        //Speed is in meter per second we are converting it to kmph
                        _speedInKphFlow.update { location.speed.toDouble() * 3.6 }
                        Log.e("Speed", location.speed.toString())

                        val distance = if (_markersFlow.value.size > 1) {
                            _markersFlow.value.zipWithNext()
                                .sumOf { (start, end) -> start.distanceTo(end).toDouble() }
                        } else {
                            0.0
                        }

                        _distanceCoveredInMeters.update { distance }

                        if (_speedInKphFlow.value > 0) {
                            _speedArray.update { it + _speedInKphFlow.value }
                        }
                    }
                }
        }
    }

    fun stopLocationUpdates() {
        locationRepository.stopLocationUpdates()
        locationUpdateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        locationUpdateJob?.cancel()
        timerJob?.cancel()
    }


    private inline fun <T1, T2, T3, T4, T5, T6, T7,T8, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7,T8) -> R
    ): Flow<R> {
        return combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6,
            flow7,
            flow8
        ) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
                args[7] as T8,
            )
        }
    }
}

