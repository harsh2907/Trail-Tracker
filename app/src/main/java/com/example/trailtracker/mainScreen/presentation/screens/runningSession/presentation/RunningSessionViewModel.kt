package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation


import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.models.RunEntity
import com.example.trailtracker.mainScreen.domain.repositories.RunSessionRepository
import com.example.trailtracker.mainScreen.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningSessionViewModel @Inject constructor(
    private val runSessionRepository: RunSessionRepository,
    private val firebaseRunRepository: FirebaseRunRepository
) : ViewModel() {


    val runSessionState = combine(
        TrackingService.coloredPolylinePoints,
        TrackingService.lastLocation,
        TrackingService.speedInKph,
        TrackingService.distanceCoveredInMeters,
        TrackingService.speedArray,
        TrackingService.isTracking,
        TrackingService.sessionDuration
    ) { polylinePoints, currentLocation, speedInMpsFlow, distanceCoveredInMetersFlow, speedArray, sessionStatus, sessionDuration ->

        val cameraPosition =
            currentLocation?.let { loc -> LatLng(loc.latitude, loc.longitude) } ?: LatLng(0.0, 0.0)

        RunSessionState(
            polylinePoints = polylinePoints,
            speedInKph = speedInMpsFlow,
            cameraPosition = cameraPosition,
            distanceCoveredInMeters = distanceCoveredInMetersFlow,
            averageSpeedInKph = if (sessionDuration == 0L) 0.0 else {
                (distanceCoveredInMetersFlow / sessionDuration) * 3.6
            },
            isTracking = sessionStatus,
            sessionDuration = sessionDuration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RunSessionState())

    fun saveSessionToFirebase(
        runEntity: RunEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = firebaseRunRepository.saveRunSession(runEntity)
            if(res.isSuccess){
                onSuccess()
            }else if(res.isFailure){
                onError(res.exceptionOrNull()?.message?:"Oops,an unknown error occurred")
            }
        }
    }

    fun saveSessionToRoomDatabase(runEntity: RunEntity) {
        viewModelScope.launch {
            runSessionRepository.upsertRun(runEntity)
        }
    }


    private inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
    ): Flow<R> {
        return combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6,
            flow7
        ) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7
            )
        }
    }
}

