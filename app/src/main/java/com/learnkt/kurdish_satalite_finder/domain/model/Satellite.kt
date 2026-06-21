package com.learnkt.kurdish_satalite_finder.domain.model

data class Satellite(
    val id: Int = 0,
    val name: String,
    val longitude: Double, // Orbital position, e.g., 42.0 for 42E, -7.0 for 7W
    val isFavorite: Boolean = false,
    val transponders: List<Transponder> = emptyList()
)

data class Transponder(
    val frequency: Int,
    val polarization: String, // H or V
    val symbolRate: Int
)

data class SatelliteCalculation(
    val azimuth: Double,
    val elevation: Double,
    val lnbSkew: Double
)
