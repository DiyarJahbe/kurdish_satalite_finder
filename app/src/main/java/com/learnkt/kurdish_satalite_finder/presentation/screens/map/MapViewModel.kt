package com.learnkt.kurdish_satalite_finder.presentation.screens.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.learnkt.kurdish_satalite_finder.domain.usecase.GetUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _cameraPosition = MutableStateFlow<CameraPosition?>(null)
    val cameraPosition: StateFlow<CameraPosition?> = _cameraPosition.asStateFlow()

    init {
        loadUserLocation()
    }

    private fun loadUserLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            getUserLocationUseCase.getLocationUpdates(context).collect { location ->
                _userLocation.value = location
                if (_cameraPosition.value == null && location != null) {
                    _cameraPosition.value = CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        15f
                    )
                }
                _isLoading.value = false
            }
        }
    }

    fun searchLocation(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            try {
                val geocoder = android.location.Geocoder(context)
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    _cameraPosition.value = CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshLocation() {
        loadUserLocation()
    }

    fun updateCameraPosition(latLng: LatLng, zoom: Float = 15f) {
        _cameraPosition.value = CameraPosition.fromLatLngZoom(latLng, zoom)
    }
}
