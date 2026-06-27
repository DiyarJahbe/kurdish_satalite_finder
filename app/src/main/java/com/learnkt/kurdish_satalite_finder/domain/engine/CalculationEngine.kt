package com.learnkt.kurdish_satalite_finder.domain.engine

import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import kotlin.math.*

/**
 * Advanced Satellite Calculation Engine.
 * Uses standard geostationary orbit formulas for Azimuth, Elevation, and Skew.
 */
object CalculationEngine {

    fun calculate(
        userLat: Double,
        userLon: Double,
        satLon: Double
    ): SatelliteCalculation {
        // Convert to radians
        val latR = Math.toRadians(userLat)
        val lonR = Math.toRadians(userLon)
        val satLonR = Math.toRadians(satLon)

        // Difference in longitude
        val dLon = satLonR - lonR

        // 1. Azimuth Calculation
        // Formula: Azimuth = 180 + atan(tan(dLon) / sin(lat))
        // We use atan2 for correct quadrant handling
        val azimuthRad = atan2(sin(dLon), tan(latR))
        var azimuthDeg = Math.toDegrees(azimuthRad) + 180.0
        
        // Normalize to [0, 360)
        azimuthDeg = (azimuthDeg + 360.0) % 360.0

        // 2. Elevation Calculation
        // Formula: Elevation = atan((cos(G) * cos(L) - 0.1512) / sqrt(1 - (cos(G)*cos(L))^2))
        // where G is dLon and L is Lat
        val r = 6371.0 // Earth radius
        val h = 35786.0 // Sat altitude
        val k = (r + h) / r // approx 6.61
        
        val cosG = cos(dLon)
        val cosL = cos(latR)
        
        val elevationRad = atan((cosG * cosL - (1 / k)) / sqrt(1 - (cosG * cosL).pow(2)))
        val elevationDeg = Math.toDegrees(elevationRad)

        // 3. LNB Skew Calculation
        // Formula: Skew = atan(sin(dLon) / tan(lat))
        val skewRad = atan(sin(dLon) / tan(latR))
        val skewDeg = Math.toDegrees(skewRad)

        return SatelliteCalculation(
            azimuth = azimuthDeg,
            elevation = elevationDeg,
            lnbSkew = skewDeg
        )
    }
}
