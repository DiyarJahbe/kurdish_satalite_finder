package com.learnkt.kurdish_satalite_finder.presentation.screens.home

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import com.learnkt.kurdish_satalite_finder.domain.usecase.GetSatellitesUseCase
import com.learnkt.kurdish_satalite_finder.domain.usecase.GetUserLocationUseCase
import com.learnkt.kurdish_satalite_finder.domain.usecase.SearchSatellitesUseCase
import com.learnkt.kurdish_satalite_finder.domain.usecase.SeedSatellitesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seedSatellitesUseCase: SeedSatellitesUseCase,
    private val getSatellitesUseCase: GetSatellitesUseCase,
    private val searchSatellitesUseCase: SearchSatellitesUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val repository: SatelliteRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    val satellites = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getSatellitesUseCase()
            } else {
                searchSatellitesUseCase(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSatellites = repository.getFavoriteSatellites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            seedSatellitesUseCase()
        }
        loadUserLocation()
    }

    private fun loadUserLocation() {
        viewModelScope.launch {
            getUserLocationUseCase.getLocationUpdates(context).collect { location ->
                _userLocation.value = location
            }
        }
    }

    fun refreshLocation() {
        loadUserLocation()
    }

    fun toggleFavorite(satellite: Satellite) {
        viewModelScope.launch {
            repository.toggleFavorite(satellite)
        }
    }
}
