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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.ColoredPolyline
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.TrackingUtils
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

/**
 * Type alias representing a list of LatLng points for polyline drawing.
 */
typealias Polyline = MutableList<LatLng>

/**
 * Type alias representing multiple polylines.
 */
typealias Polylines = MutableList<Polyline>

/**
 * Type alias representing a list of ColoredPolyline, which contains a polyline with color information.
 */
typealias ColoredPolylines = MutableList<ColoredPolyline>

/**
 * [TrackingService] is a foreground service responsible for tracking user location data, updating
 * distance, speed, and managing notification UI during active tracking sessions.
 */
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
        val isTracking = MutableStateFlow(false)  // Tracks whether service is actively tracking.
        val lastLocation = MutableStateFlow<Location?>(null)  // Holds the last known location.
        val pathPoints =
            MutableStateFlow<Polylines>(mutableListOf())  // Stores all tracked polylines.
        val coloredPolylinePoints =
            MutableStateFlow<ColoredPolylines>(mutableListOf())  // Stores polylines with speed-based color coding.
        val speedInKph = MutableStateFlow(0.0)  // Current speed in kilometers per hour.
        val distanceCoveredInMeters = MutableStateFlow(0.0)  // Total distance covered in meters.
        val speedArray = MutableStateFlow<List<Double>>(emptyList())  // List of speed values.
        val sessionDuration = MutableStateFlow(0L)  // Duration of the current session in seconds.

        var isServiceActive = false
            private set  // Indicates whether the service is active.

        /**
         * Resets all mutable states and clears any ongoing tracking data.
         */
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

        startLocationTracking()  // Initializes location tracking.

        // Launches a coroutine to monitor tracking state and process location updates.
        trackingJob?.cancel()
        trackingJob = lifecycleScope.launch {
            isTracking.collectLatest { tracking ->
                updateNotificationTrackingState(tracking)

                while (tracking) {
                    delay(2000)
                    processLatestPathPoint()
                }
            }
        }
    }


    /**
     * Processes the points and updates all variables based on the path.
     */
    private fun processLatestPathPoint() {
        pathPoints.value.let { points ->

            if (points.isNotEmpty() && points.last().size > 1) {

                //Fetching last two points from last array to get the speed and distance
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
                    speed <= 5 -> Color.Red.toArgb()
                    speed <= 10 -> Color.Yellow.toArgb()
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

    /**
     * Handles intent actions such as starting, pausing, or stopping the service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.START_OR_RESUME_SERVICE -> startOrResumeService()
                Constants.PAUSE_SERVICE -> pauseService()
                Constants.STOP_SERVICE -> stopService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Starts or resumes the service, sets tracking state to active, and initiates the timer.
     */
    private fun startOrResumeService() {
        if (isRunningFirstTime) {
            isRunningFirstTime = false
            startForegroundService()
        }
        isTracking.update { true }
        isServiceActive = true
        startOrResumeTimer()
    }

    /**
     * Pauses the service, stops the timer, and adds an empty polyline to separate tracking segments.
     */
    private fun pauseService() {
        isTracking.update { false }
        pauseTimer()
        addEmptyPolyline()
    }

    /**
     * Stops the service, resets tracking states, stops location updates, and clears notifications.
     */
    private fun stopService() {
        isTracking.update { false }
        stopTimer()
        isRunningFirstTime = true
        fusedLocationClient.removeLocationUpdates(locationCallback)
        trackingJob?.cancel()
        resetStates()
        stopSelf()
    }

    /**
     * Initializes or resumes the timer that tracks session duration.
     */
    private fun startOrResumeTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (true) {
                delay(1000)
                sessionDuration.update { it + 1 }
            }
        }
    }

    /**
     * Cancels the timer job, effectively pausing the timer.
     */
    private fun pauseTimer() {
        timerJob?.cancel()
    }

    /**
     * Stops and resets the timer by setting session duration to zero.
     */
    private fun stopTimer() {
        sessionDuration.update { 0 }
        timerJob?.cancel()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.lastLocation?.let { lastLoc ->
                lastLocation.update { lastLoc }
            }

            if (isTracking.value) {
                result.locations.forEach { location -> addPointToPath(location) }
            }
        }
    }

    /**
     * Starts location tracking by requesting location updates with high accuracy.
     */
    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (TrackingUtils.hasAllPermissions(this)) {
            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    /**
     * Adds an empty polyline to the path, used to separate segments in tracking.
     */
    private fun addEmptyPolyline() {
        pathPoints.update { points -> points.apply { add(mutableListOf()) } }
    }

    /**
     * Adds a point to the current path, recording the current location coordinates.
     */
    private fun addPointToPath(location: Location) {
        val pos = LatLng(location.latitude, location.longitude)
        pathPoints.update { points -> points.apply { last().add(pos) } }
    }

    /**
     * Updates the notification with current tracking state and action button (Pause/Resume).
     */
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            Intent(this, TrackingService::class.java).apply { action = Constants.PAUSE_SERVICE }
        } else {
            Intent(this, TrackingService::class.java).apply {
                action = Constants.START_OR_RESUME_SERVICE
            }
        }

        val icon = if (isTracking) R.drawable.ic_pause else R.drawable.ic_play
        curNotificationBuilder = baseNotificationBuilder.clearActions()
            .addAction(
                icon,
                notificationActionText,
                PendingIntent.getService(
                    this,
                    1,
                    pendingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            Constants.NOTIFICATION_ID,
            curNotificationBuilder.build()
        )
    }

    /**
     * Starts the service in the foreground, creating and displaying a notification.
     */
    private fun startForegroundService() {
        addEmptyPolyline()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())

        //Updates the time in notification
        lifecycleScope.launch {
            sessionDuration.collectLatest { time ->
                val notification = curNotificationBuilder
                    .setContentText(Constants.formatTime(time))
                    .build()

                notificationManager.notify(Constants.NOTIFICATION_ID, notification)
            }
        }
    }

    /**
     * Creates a notification channel for displaying service notifications.
     */
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
