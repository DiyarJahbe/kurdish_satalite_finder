package com.learnkt.kurdish_satalite_finder.domain.usecase

import android.location.Location
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class CalculateSatellitePositionUseCase @Inject constructor() {
    operator fun invoke(
        satellite: Satellite,
        userLocation: Location
    ): SatelliteCalculation {
        val userLat = Math.toRadians(userLocation.latitude)
        val userLon = Math.toRadians(userLocation.longitude)
        val satLon = Math.toRadians(satellite.longitude)

        // Calculate azimuth and elevation
        val deltaLon = satLon - userLon
        
        val x = sin(deltaLon)
        val y = cos(deltaLon) * sin(userLat) - tan(0.0) * cos(userLat) // Assuming geostationary at 0 latitude
        
        val azimuthRad = atan2(x, y)
        val azimuth = Math.toDegrees(azimuthRad).toDouble()
        val normalizedAzimuth = if (azimuth < 0) azimuth + 360 else azimuth

        val elevationRad = atan2(
            cos(deltaLon) * cos(userLat) * cos(0.0) - sin(userLat) * sin(0.0),
            sin(deltaLon) * cos(userLat)
        )
        val elevation = Math.toDegrees(elevationRad).toDouble()

        // Calculate LNB skew (polarization angle)
        val lnbSkew = Math.toDegrees(atan2(sin(deltaLon), tan(userLat)))

        return SatelliteCalculation(
            azimuth = normalizedAzimuth,
            elevation = elevation,
            lnbSkew = lnbSkew
        )
    }
}
