package com.learnkt.kurdish_satalite_finder.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.usecase.LocationSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val locationSettingsUseCase: LocationSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationSettingsUiState())
    val uiState: StateFlow<LocationSettingsUiState> = combine(
        locationSettingsUseCase.useAutoGps,
        locationSettingsUseCase.manualLatitude,
        locationSettingsUseCase.manualLongitude,
        locationSettingsUseCase.selectedCity,
        _uiState
    ) { useGps, lat, lon, city, state ->
        state.copy(
            useAutoGps = useGps,
            manualLatitude = if (state.useAutoGps) lat.toString() else state.manualLatitude,
            manualLongitude = if (state.useAutoGps) lon.toString() else state.manualLongitude,
            selectedCity = city
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocationSettingsUiState())

    fun setUseAutoGps(enabled: Boolean) {
        viewModelScope.launch {
            locationSettingsUseCase.updateSettings(useAutoGps = enabled)
        }
    }

    fun updateManualLatitude(lat: String) {
        _uiState.update { it.copy(manualLatitude = lat) }
    }

    fun updateManualLongitude(lon: String) {
        _uiState.update { it.copy(manualLongitude = lon) }
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
