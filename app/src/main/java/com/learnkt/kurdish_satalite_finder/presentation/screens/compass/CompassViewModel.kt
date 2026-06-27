package com.learnkt.kurdish_satalite_finder.presentation.screens.compass

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.core.CompassSensorManager
import com.learnkt.kurdish_satalite_finder.core.LocationProvider
import com.learnkt.kurdish_satalite_finder.domain.engine.CalculationEngine
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
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

    private val _targetAzimuth = MutableStateFlow(0f)
    val targetAzimuth = _targetAzimuth.asStateFlow()

    val currentAzimuth = sensorManager.getRotationFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        loadSatellite()
    }

    fun loadSatellite() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location == null) return@launch

            repository.getAllSatellites().map { list ->
                list.find { it.id == satelliteId }
            }.collect { sat ->
                _satellite.value = sat
                sat?.let {
                    val calc = CalculationEngine.calculate(
                        location.latitude,
                        location.longitude,
                        it.longitude
                    )
                    _targetAzimuth.value = calc.azimuth.toFloat()
                }
            }
        }
    }
}
