package com.learnkt.kurdish_satalite_finder.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.usecase.LocationSettingsUseCase
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationSettingsUiState(
    val useAutoGps: Boolean = true,
    val manualLatitude: String = "36.1911",
    val manualLongitude: String = "44.0092",
    val selectedCity: String? = null
)

@HiltViewModel
class LocationSettingsViewModel @Inject constructor(
    private val locationSettingsUseCase: LocationSettingsUseCase,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationSettingsUiState())
    val uiState: StateFlow<LocationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            val useGps = locationSettingsUseCase.useAutoGps.first()
            val lat = locationSettingsUseCase.manualLatitude.first()
            val lon = locationSettingsUseCase.manualLongitude.first()
            val city = locationSettingsUseCase.selectedCity.first()
            
            _uiState.value = LocationSettingsUiState(
                useAutoGps = useGps,
                manualLatitude = lat.toString(),
                manualLongitude = lon.toString(),
                selectedCity = city
            )
        }
    }

    fun setUseAutoGps(enabled: Boolean) {
        _uiState.update { it.copy(useAutoGps = enabled) }
    }

    fun updateManualLatitude(lat: String) {
        _uiState.update { it.copy(manualLatitude = lat, selectedCity = null) }
    }

    fun updateManualLongitude(lon: String) {
        _uiState.update { it.copy(manualLongitude = lon, selectedCity = null) }
    }

    fun searchLocation(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            try {
                val geocoder = android.location.Geocoder(context)
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    _uiState.update {
                        it.copy(
                            manualLatitude = address.latitude.toString(),
                            manualLongitude = address.longitude.toString(),
                            selectedCity = address.locality ?: address.featureName
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onMapClick(latLng: com.google.android.gms.maps.model.LatLng) {
        _uiState.update { 
            it.copy(
                manualLatitude = latLng.latitude.toString(),
                manualLongitude = latLng.longitude.toString(),
                selectedCity = null
            )
        }
    }

    fun selectCity(city: KurdishCities.City) {
        _uiState.update { 
            it.copy(
                manualLatitude = city.lat.toString(),
                manualLongitude = city.lon.toString(),
                selectedCity = city.name
            )
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val lat = _uiState.value.manualLatitude.toDoubleOrNull() ?: 36.1911
            val lon = _uiState.value.manualLongitude.toDoubleOrNull() ?: 44.0092
            locationSettingsUseCase.updateSettings(
                useAutoGps = _uiState.value.useAutoGps,
                latitude = lat,
                longitude = lon,
                city = _uiState.value.selectedCity
            )
        }
    }
}
