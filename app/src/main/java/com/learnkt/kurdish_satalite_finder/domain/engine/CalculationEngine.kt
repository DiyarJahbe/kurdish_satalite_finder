package com.learnkt.kurdish_satalite_finder.domain.engine

import com.learnkt.kurdish_satalite_finder.domain.model.SatelliteCalculation
import kotlin.math.*

object CalculationEngine {

    private const val RATIO = 6378.14 / 42164.0

    fun calculate(
        userLat: Double,
        userLon: Double,
        satLon: Double
    ): SatelliteCalculation {

        val lat = Math.toRadians(userLat)
        val deltaLon = Math.toRadians(satLon - userLon)

        // Elevation
        val cosPsi = cos(lat) * cos(deltaLon)
        val elevation = Math.toDegrees(
            atan(
                (cosPsi - RATIO) /
                        sqrt(1.0 - cosPsi * cosPsi)
            )
        )

        // Azimuth
        val x = Math.toDegrees(
            atan(
                tan(abs(deltaLon)) / sin(lat)
            )
        )

        val azimuth =
            if (userLat >= 0) {
                if (satLon > userLon)
                    180.0 - x
                else
                    180.0 + x
            } else {
                if (satLon > userLon)
                    x
                else
                    360.0 - x
            }

        // LNB Skew
        val skew = Math.toDegrees(
            atan(
                sin(deltaLon) / tan(lat)
            )
        )

        return SatelliteCalculation(
            azimuth = azimuth,
            elevation = elevation,
            lnbSkew = skew
        )
    }
}