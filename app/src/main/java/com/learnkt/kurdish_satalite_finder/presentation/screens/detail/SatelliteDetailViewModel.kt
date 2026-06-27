package com.learnkt.kurdish_satalite_finder.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class SatelliteDetailViewModel @Inject constructor(
    private val repository: SatelliteRepository,
    private val locationProvider: LocationProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val satelliteId: Int = checkNotNull(savedStateHandle["satelliteId"])

    private val _satellite = MutableStateFlow<Satellite?>(null)
    val satellite = _satellite.asStateFlow()

    private val _calculations = MutableStateFlow<SatelliteCalculation?>(null)
    val calculations = _calculations.asStateFlow()

    private val _locationError = MutableStateFlow(false)
    val locationError = _locationError.asStateFlow()

    init {
        loadSatellite()
    }

    fun loadSatellite() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            if (location == null) {
                _locationError.value = true
                return@launch
            }
            _locationError.value = false

            repository.getAllSatellites().map { list ->
                list.find { it.id == satelliteId }
            }.collect { sat ->
                _satellite.value = sat
                sat?.let {
                    _calculations.value = CalculationEngine.calculate(
                        location.latitude,
                        location.longitude,
                        it.longitude
                    )
                }
            }
        }
    }
}
