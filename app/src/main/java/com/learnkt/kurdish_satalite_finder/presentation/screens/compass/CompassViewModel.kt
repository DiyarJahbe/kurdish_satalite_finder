package com.learnkt.kurdish_satalite_finder.presentation.screens.compass

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.core.CompassSensorManager
import com.learnkt.kurdish_satalite_finder.core.LocationProvider
import com.learnkt.kurdish_satalite_finder.domain.engine.CalculationEngine
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompassViewModel @Inject constructor(
    private val repository: SatelliteRepository,
    private val sensorManager: CompassSensorManager,
    private val locationProvider: LocationProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val satelliteId: Int = checkNotNull(savedStateHandle["satelliteId"])

    private val _satellite = MutableStateFlow<Satellite?>(null)
    val satellite = _satellite.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _calculation = MutableStateFlow<SatelliteCalculation?>(null)
    val calculation = _calculation.asStateFlow()

    val currentAzimuth = sensorManager.getRotationFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        loadSatellite()
        startLocationUpdates()
    }

    fun loadSatellite() {
        viewModelScope.launch {
            repository.getAllSatellites().map { list ->
                list.find { it.id == satelliteId }
            }.collect { sat ->
                _satellite.value = sat
                recalculate()
            }
        }
    }

    fun startLocationUpdates() {
        locationProvider.getLocationFlow()
            .onEach { location ->
                _userLocation.value = location
                recalculate()
            }
            .launchIn(viewModelScope)
    }

    private fun recalculate() {
        val sat = _satellite.value ?: return
        val loc = _userLocation.value ?: return
        
        _calculation.value = CalculationEngine.calculate(
            loc.latitude,
            loc.longitude,
            sat.longitude
        )
    }
}
