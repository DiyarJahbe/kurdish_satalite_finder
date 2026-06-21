package com.learnkt.kurdish_satalite_finder.data.mapper

import com.learnkt.kurdish_satalite_finder.data.local.entity.SatelliteEntity
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite

fun SatelliteEntity.toSatellite(): Satellite {
    return Satellite(
        id = id,
        name = name,
        longitude = longitude,
        isFavorite = isFavorite
    )
}

fun Satellite.toSatelliteEntity(): SatelliteEntity {
    return SatelliteEntity(
        id = id,
        name = name,
        longitude = longitude,
        isFavorite = isFavorite
    )
}
