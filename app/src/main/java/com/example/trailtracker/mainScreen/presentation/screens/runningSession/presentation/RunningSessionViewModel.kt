package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.services.Polylines
import com.example.trailtracker.mainScreen.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RunningSessionViewModel : ViewModel() {

    private val _locationFlow = MutableStateFlow<Location?>(null)
    private val _polylinePointsFlow = MutableStateFlow<Polylines>(mutableListOf())
    private val _cameraPositionFlow = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    private val _speedInKphFlow = MutableStateFlow<Double>(0.0)
    private val _distanceCoveredInMeters = MutableStateFlow(0.0)
    private val _speedArray = MutableStateFlow<List<Double>>(emptyList())
    private val _isTracking = MutableStateFlow(false)
    private val _sessionDuration = MutableStateFlow(0L)

    val runSessionState = combine(
        _locationFlow,
        _polylinePointsFlow,
        _cameraPositionFlow,
        _speedInKphFlow,
        _distanceCoveredInMeters,
        _speedArray,
        _isTracking,
        _sessionDuration
    ) { locationFlow, polylinePoints, cameraPositionFlow, speedInMpsFlow, distanceCoveredInMetersFlow, speedArray, sessionStatus, sessionDuration ->

        RunSessionState(
            liveLocation = locationFlow,
            polylinePoints = polylinePoints,
            speedInKph = speedInMpsFlow,
            cameraPosition = cameraPositionFlow,
            distanceCoveredInMeters = distanceCoveredInMetersFlow,
            averageSpeedInKph = if (speedArray.isNotEmpty()) speedArray.average() else 0.0,
            isTracking = sessionStatus,
            sessionDuration = sessionDuration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RunSessionState())


    init {
        getLocationUpdates()
    }


    fun getLocationUpdates() {
        viewModelScope.launch {
            combine(
                TrackingService.isTracking,
                TrackingService.pathPoints,
                TrackingService.lastLocation,
                TrackingService.sessionDuration
            ) { isTracking, pathPoints, lastLocation, sessionDuration ->
                _isTracking.update { isTracking }
                _polylinePointsFlow.update { pathPoints }
                _sessionDuration.update { sessionDuration }


                lastLocation?.let { loc ->
                    _cameraPositionFlow.update {
                        LatLng(loc.latitude, loc.longitude)
                    }
                }

                if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {

                    val preLastPoint = pathPoints.last()[pathPoints.last().size - 2]
                    val lastPoint = pathPoints.last().last()

                    val results = FloatArray(1)
                    Location.distanceBetween(
                        preLastPoint.latitude,
                        preLastPoint.longitude,
                        lastPoint.latitude,
                        lastPoint.longitude,
                        results
                    )
                    val distance = results[0]

                    // Time interval is 2 seconds
                    val timeIntervalInSeconds = 2.0

                    // Calculate speed in m/s
                    val speed = distance / timeIntervalInSeconds

                    // Convert speed to km/h
                    val speedInKph = speed * 3.6

                    // Update flows
                    _speedInKphFlow.update { speedInKph }
                    _distanceCoveredInMeters.update { it + distance }
                    _speedArray.update { it + speedInKph }

                }
            }.collectLatest {}
        }
    }


    private inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
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

