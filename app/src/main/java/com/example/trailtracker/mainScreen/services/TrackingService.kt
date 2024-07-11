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
import com.example.trailtracker.MainActivity
import com.example.trailtracker.R
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.TrackingUtils
import com.example.trailtracker.utils.formatTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

data class ColoredPolyline(
    val points: MutableList<LatLng>,
    val color: Int
)

typealias ColoredPolylines = MutableList<ColoredPolyline>

class TrackingService : LifecycleService() {

    private var isRunningFirstTime = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var timerJob: Job? = null
    private var trackingJob: Job? = null

    companion object {
        const val TAG = "Tracking Service"
        val isTracking = MutableStateFlow(false)
        val lastLocation = MutableStateFlow<Location?>(null)
        val pathPoints = MutableStateFlow<Polylines>(mutableListOf())
        val coloredPolylinePoints = MutableStateFlow<ColoredPolylines>(mutableListOf())
        val speedInKph = MutableStateFlow<Double>(0.0)
        val distanceCoveredInMeters = MutableStateFlow(0.0)
        val speedArray = MutableStateFlow<List<Double>>(emptyList())
        val sessionDuration = MutableStateFlow(0L)

        fun resetStates(){
            lastLocation.update { null }
            pathPoints.update { mutableListOf() }
            coloredPolylinePoints.update { mutableListOf() }
            distanceCoveredInMeters.update { 0.0 }
        }
    }


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationTracking()

        trackingJob?.cancel()
        trackingJob = lifecycleScope.launch {
            isTracking.collectLatest {
                while (it){
                    delay(2000)
                    pathPoints.value.let { points ->
                        Log.e("Collected Points",points.toString())
                        if (points.isNotEmpty() && points.last().size > 1) {
                            val preLastPoint = points.last().dropLast(1).last()
                            val lastPoint = points.last().last()

                            Log.e("Pre and last", "$preLastPoint $lastPoint")


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

        lifecycleScope.launch {
            sessionDuration.collectLatest { time ->
                updateNotification(time.formatTime())
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.START_OR_RESUME_SERVICE -> {

                    if (isRunningFirstTime) {
                        startForegroundService()
                        isRunningFirstTime = false
                    }

                    startOrResumeTimer()
                    isTracking.update { true }

                }

                Constants.PAUSE_SERVICE -> {
                    isTracking.update { false }
                    pauseTimer()
                    addEmptyPolyline()
                }

                Constants.STOP_SERVICE -> {
                    isTracking.update { false }
                    stopTimer()
                    isRunningFirstTime = true

                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    trackingJob?.cancel()
                    stopSelf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
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


            result.lastLocation?.let { lastLoc ->
                lastLocation.update { lastLoc }
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
        }
    }

    private fun addEmptyPolyline() {
        pathPoints.update {
            it.add(mutableListOf())
            it
        }
    }

    //Add Position to the last of the list
    private fun addPointToPath(location: Location) {
        val pos = LatLng(location.latitude, location.longitude)
        Log.e("Path", pos.toString())
        pathPoints.update { points ->
            points.last().add(pos)
            points
        }
    }

    private fun startForegroundService() {

        addEmptyPolyline()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.running_person)
                .setContentTitle("Running Session")
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent())
                .build()

        startForeground(Constants.NOTIFICATION_ID, notification)

    }

    private fun getMainActivityPendingIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).apply {
            this.action = Constants.ACTION_SHOW_TRACKING_SCREEN
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(time: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.running_person)
                .setContentTitle("Running Session")
                .setContentText(time)
                .setContentIntent(getMainActivityPendingIntent())
                .build()

        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        trackingJob?.cancel()
    }

}