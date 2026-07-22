package com.learnkt.kurdish_satalite_finder.presentation.screens.ar

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import com.learnkt.kurdish_satalite_finder.domain.usecase.CalculateSatellitePositionUseCase
import com.learnkt.kurdish_satalite_finder.domain.usecase.GetSatelliteByIdUseCase
import com.learnkt.kurdish_satalite_finder.domain.usecase.GetUserLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ARViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSatelliteByIdUseCase: GetSatelliteByIdUseCase,
    private val calculateSatellitePositionUseCase: CalculateSatellitePositionUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val _satellite = MutableStateFlow<Satellite?>(null)
    val satellite: StateFlow<Satellite?> = _satellite.asStateFlow()

    private val _calculation = MutableStateFlow<SatelliteCalculation?>(null)
    val calculation: StateFlow<SatelliteCalculation?> = _calculation.asStateFlow()

    private val _currentAzimuth = MutableStateFlow(0f)
    val currentAzimuth: StateFlow<Float> = _currentAzimuth.asStateFlow()

    private val _currentPitch = MutableStateFlow(0f)
    val currentPitch: StateFlow<Float> = _currentPitch.asStateFlow()

    private val _currentRoll = MutableStateFlow(0f)
    val currentRoll: StateFlow<Float> = _currentRoll.asStateFlow()

    private var satelliteId: Int? = null

    fun loadSatellite(satelliteId: Int) {
        this.satelliteId = satelliteId
        viewModelScope.launch {
            _satellite.value = getSatelliteByIdUseCase(satelliteId)
            calculatePosition()
        }
    }

    private suspend fun calculatePosition() {
        val satellite = _satellite.value ?: return
        val location = getUserLocationUseCase( context)
        
        if (location != null) {
            _calculation.value = calculateSatellitePositionUseCase(
                satellite = satellite,
                userLocation = location
            )
        }
    }

    fun startSensors() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
    }

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    gravity = it.values.clone()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    geomagnetic = it.values.clone()
                }
            }

            if (gravity != null && geomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)
                
                val success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)
                
                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    
                    // Convert radians to degrees
                    val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                    val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()
                    
                    // Normalize azimuth to 0-360
                    val normalizedAzimuth = if (azimuth < 0) azimuth + 360 else azimuth
                    
                    _currentAzimuth.value = normalizedAzimuth
                    _currentPitch.value = pitch
                    _currentRoll.value = roll
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }

    override fun onCleared() {
        super.onCleared()
        stopSensors()
    }
}
