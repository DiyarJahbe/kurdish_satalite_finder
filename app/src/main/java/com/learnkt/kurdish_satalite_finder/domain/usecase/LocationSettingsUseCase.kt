package com.learnkt.kurdish_satalite_finder.domain.usecase

import com.learnkt.kurdish_satalite_finder.data.preferences.LocationPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationSettingsUseCase @Inject constructor(
    private val preferences: LocationPreferences
) {
    val useAutoGps: Flow<Boolean> = preferences.useAutoGps
    val manualLatitude: Flow<Double> = preferences.manualLatitude
    val manualLongitude: Flow<Double> = preferences.manualLongitude
    val selectedCity: Flow<String?> = preferences.selectedCity

    suspend fun updateSettings(
        useAutoGps: Boolean,
        latitude: Double? = null,
        longitude: Double? = null,
        city: String? = null
    ) {
        preferences.setLocationSettings(useAutoGps, latitude, longitude, city)
    }
}
