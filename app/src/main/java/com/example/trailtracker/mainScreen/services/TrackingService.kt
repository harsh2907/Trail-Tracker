package com.example.trailtracker.mainScreen.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.ColoredPolyline
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.TrackingUtils
import com.example.trailtracker.utils.formatTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

typealias ColoredPolylines = MutableList<ColoredPolyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isRunningFirstTime = true


    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null
    private var trackingJob: Job? = null

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    companion object {
        val isTracking = MutableStateFlow(false)
        val lastLocation = MutableStateFlow<Location?>(null)
        val pathPoints = MutableStateFlow<Polylines>(mutableListOf())
        val coloredPolylinePoints = MutableStateFlow<ColoredPolylines>(mutableListOf())
        val speedInKph = MutableStateFlow(0.0)
        val distanceCoveredInMeters = MutableStateFlow(0.0)
        val speedArray = MutableStateFlow<List<Double>>(emptyList())
        val sessionDuration = MutableStateFlow(0L)

        var isServiceActive = false
            private set

        fun resetStates() {
            lastLocation.update { null }
            pathPoints.update { mutableListOf() }
            coloredPolylinePoints.update { mutableListOf() }
            distanceCoveredInMeters.update { 0.0 }
            speedInKph.update { 0.0 }
            isServiceActive = false
        }
    }



    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder

        startLocationTracking()

        lifecycleScope.launch {
            isTracking.collectLatest {
                updateNotificationTrackingState(it)
            }
        }

        trackingJob?.cancel()
        trackingJob = lifecycleScope.launch {
            isTracking.collectLatest {tracking->
                while (tracking) {
                    delay(2000)
                    pathPoints.value.let { points ->

                        if (points.isNotEmpty() && points.last().size > 1) {
                            val preLastPoint = points.last().dropLast(1).last()
                            val lastPoint = points.last().last()

                            val results = FloatArray(1)
                            Location.distanceBetween(
                                preLastPoint.latitude,
                                preLastPoint.longitude,
                                lastPoint.latitude,
                                lastPoint.longitude,
                                results
                            )
                            val distance = results[0]

                            // Calculate speed in m/s and convert to km/h
                            val speed = (distance / 2.0) * 3.6

                            // Determine color based on speed
                            val color = when {
                                speed <= 8 -> Color.Red.toArgb()
                                speed <= 14 -> Color.Yellow.toArgb()
                                else -> Color.Green.toArgb()
                            }

                            // Create a ColoredPolyline and update the state
                            val coloredPoint =
                                ColoredPolyline(mutableListOf(preLastPoint, lastPoint), color)
                            coloredPolylinePoints.update { it.apply { add(coloredPoint) } }

                            // Update flows
                            speedInKph.update { speed }
                            distanceCoveredInMeters.update { it + distance }
                            speedArray.update { it + speed }
                        }
                    }
                }
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.START_OR_RESUME_SERVICE -> {
                    startOrResumeService()
                }

                Constants.PAUSE_SERVICE -> {
                    pauseService()
                }

                Constants.STOP_SERVICE -> {
                    stopService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startOrResumeService() {

        if (isRunningFirstTime) {
            startForegroundService()
            isRunningFirstTime = false
        }

        startOrResumeTimer()
        isTracking.update { true }
        isServiceActive = true
    }

    private fun pauseService() {
        isTracking.update { false }
        pauseTimer()
        addEmptyPolyline()
    }

    private fun stopService() {
        isTracking.update { false }
        stopTimer()
        isRunningFirstTime = true

        fusedLocationClient.removeLocationUpdates(locationCallback)
        trackingJob?.cancel()
        resetStates()
        stopSelf()
    }

    private fun startOrResumeTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (true) {
                delay(1000)
                sessionDuration.update { it + 1 }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun stopTimer() {
        sessionDuration.update { 0 }
        timerJob?.cancel()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            Log.e("TrackingService",result.locations.size.toString())


            result.lastLocation?.let { lastLoc ->
                lastLocation.update { lastLoc }
                Log.e("TrackingService",lastLoc.toString())
            }

            if (isTracking.value) {
                result.locations.forEach { location ->
                    if (location.accuracy < 10) {
                        addPointToPath(location)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (TrackingUtils.hasAllPermissions(this)) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000
            ).build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }else{
            Log.e("TrackingService","Permission not granted")
        }
    }

    private fun addEmptyPolyline() {
        pathPoints.update { points ->
            points.apply { add(mutableListOf()) }
        }
    }

    //Add Position to the last of the list
    private fun addPointToPath(location: Location) {
        val pos = LatLng(location.latitude, location.longitude)

        pathPoints.update { points ->
            points.apply { last().add(pos) }
        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = Constants.PAUSE_SERVICE
            }
            PendingIntent.getService(
                this,
                1,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = Constants.START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(
                this,
                2,
                resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val icon = if (isTracking) R.drawable.ic_pause else R.drawable.ic_play

        curNotificationBuilder = baseNotificationBuilder
            .clearActions()
            .addAction(icon, notificationActionText, pendingIntent)
        notificationManager.notify(Constants.NOTIFICATION_ID, curNotificationBuilder.build())
    }


    private fun startForegroundService() {

        addEmptyPolyline()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())

        lifecycleScope.launch {
            sessionDuration.collectLatest { time ->
                val notification = curNotificationBuilder
                    .setContentText(time.formatTime())
                notificationManager.notify(Constants.NOTIFICATION_ID, notification.build())
            }
        }
    }


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        trackingJob?.cancel()
    }

}