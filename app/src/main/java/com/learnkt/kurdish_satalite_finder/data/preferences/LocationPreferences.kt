package com.learnkt.kurdish_satalite_finder.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.locationDataStore: DataStore<Preferences> by preferencesDataStore(name = "location_prefs")

class LocationPreferences(private val context: Context) {
    
    companion object {
        private val USE_AUTO_GPS = booleanPreferencesKey("use_auto_gps")
        private val MANUAL_LATITUDE = doublePreferencesKey("manual_latitude")
        private val MANUAL_LONGITUDE = doublePreferencesKey("manual_longitude")
        private val SELECTED_CITY = stringPreferencesKey("selected_city")
    }
    
    val useAutoGps: Flow<Boolean> = context.locationDataStore.data.map { preferences ->
        preferences[USE_AUTO_GPS] ?: true
    }
    
    val manualLatitude: Flow<Double> = context.locationDataStore.data.map { preferences ->
        preferences[MANUAL_LATITUDE] ?: 36.1911 // Default Erbil
    }
    
    val manualLongitude: Flow<Double> = context.locationDataStore.data.map { preferences ->
        preferences[MANUAL_LONGITUDE] ?: 44.0092
    }
    
    val selectedCity: Flow<String?> = context.locationDataStore.data.map { preferences ->
        preferences[SELECTED_CITY]
    }
    
    suspend fun setLocationSettings(
        useAutoGps: Boolean,
        latitude: Double? = null,
        longitude: Double? = null,
        city: String? = null
    ) {
        context.locationDataStore.edit { preferences ->
            preferences[USE_AUTO_GPS] = useAutoGps
            latitude?.let { preferences[MANUAL_LATITUDE] = it }
            longitude?.let { preferences[MANUAL_LONGITUDE] = it }
            if (city != null) {
                preferences[SELECTED_CITY] = city
            } else {
                preferences.remove(SELECTED_CITY)
            }
        }
    }
}
