package com.example.trailtracker.mainScreen.data

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var locationCallback: LocationCallback? = null
    private var lastPoint = MutableStateFlow<LatLng?>(null)

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {loc->
                lastPoint.update { LatLng(loc.latitude, loc.longitude) }
                println("Latitude: ${loc.latitude}, Longitude: ${loc.longitude}")
            }
        }
    }

    fun lastLocation() = lastPoint.asStateFlow()

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    coroutineScope.launch {
                        delay(1000)
                        if (lastPoint.value != null) {
                            lastPoint.update { LatLng(location.latitude, location.longitude) }
                        }
                        if (location.accuracy < 20) { // Filter out inaccurate data
                            trySend(location)
                        }
                    }
                }
            }
        }


        locationCallback?.let { callback->
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        }

        awaitClose {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
        }

    }.conflate()

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}

