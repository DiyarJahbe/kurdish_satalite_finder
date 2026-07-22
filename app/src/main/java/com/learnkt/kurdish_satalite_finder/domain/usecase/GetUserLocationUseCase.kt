package com.learnkt.kurdish_satalite_finder.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.learnkt.kurdish_satalite_finder.data.preferences.LocationPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetUserLocationUseCase @Inject constructor(
    private val locationPreferences: LocationPreferences
) {
    suspend operator fun invoke(context: Context): Location? {
        val useAutoGps = locationPreferences.useAutoGps.first()
        if (!useAutoGps) {
            val lat = locationPreferences.manualLatitude.first()
            val lon = locationPreferences.manualLongitude.first()
            return Location("manual").apply {
                latitude = lat
                longitude = lon
            }
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            
            val cancellationTokenSource = CancellationTokenSource()
            val currentLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            
            currentLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    fun getLocationUpdates(context: Context): Flow<Location?> = callbackFlow {
        val useAutoGps = locationPreferences.useAutoGps.first()
        if (!useAutoGps) {
            val lat = locationPreferences.manualLatitude.first()
            val lon = locationPreferences.manualLongitude.first()
            trySend(Location("manual").apply {
                latitude = lat
                longitude = lon
            })
            close()
            return@callbackFlow
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, context.mainLooper)
        } catch (e: SecurityException) {
            trySend(null)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}

