package com.learnkt.kurdish_satalite_finder.domain.engine

import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import kotlin.math.*

object CalculationEngine {

    private const val EARTH_RADIUS = 6378.137
    private const val SATELLITE_ALTITUDE = 35786.0
    private const val RATIO = EARTH_RADIUS / (EARTH_RADIUS + SATELLITE_ALTITUDE)

    fun calculate(
        userLat: Double,
        userLon: Double,
        satLon: Double
    ): SatelliteCalculation {
        val latRad = Math.toRadians(userLat)
        val lonRad = Math.toRadians(userLon)
        val satLonRad = Math.toRadians(satLon)
        
        val diffLon = satLonRad - lonRad

        // Azimuth
        val azimuth = 180 + Math.toDegrees(atan2(sin(diffLon), tan(latRad)))
        
        // Elevation
        val cosDiffLon = cos(diffLon)
        val cosLat = cos(latRad)
        val elevation = Math.toDegrees(
            atan((cosDiffLon * cosLat - RATIO) / sqrt(1 - (cosDiffLon * cosLat).pow(2)))
        )

        // LNB Skew
        val skew = Math.toDegrees(atan(sin(diffLon) / tan(latRad)))

        return SatelliteCalculation(
            azimuth = (azimuth + 360) % 360,
            elevation = elevation,
            lnbSkew = skew
        )
    }
}
