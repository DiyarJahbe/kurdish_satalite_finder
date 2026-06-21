package com.learnkt.kurdish_satalite_finder.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.engine.CalculationEngine
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SatelliteDetailViewModel @Inject constructor(
    private val repository: SatelliteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val satelliteId: Int = checkNotNull(savedStateHandle["satelliteId"])

    private val _satellite = MutableStateFlow<Satellite?>(null)
    val satellite = _satellite.asStateFlow()

    private val _calculations = MutableStateFlow<SatelliteCalculation?>(null)
    val calculations = _calculations.asStateFlow()

    // Default location (e.g., Erbil, Kurdistan)
    private val userLat = 36.1911
    private val userLon = 44.0092

    init {
        loadSatellite()
    }

    private fun loadSatellite() {
        repository.getAllSatellites().map { list ->
            list.find { it.id == satelliteId }
        }.onEach { sat ->
            _satellite.value = sat
            sat?.let {
                _calculations.value = CalculationEngine.calculate(userLat, userLon, it.longitude)
            }
        }.launchIn(viewModelScope)
    }
}
