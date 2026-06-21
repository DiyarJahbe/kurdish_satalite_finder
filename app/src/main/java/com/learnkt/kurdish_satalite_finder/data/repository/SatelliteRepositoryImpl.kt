package com.learnkt.kurdish_satalite_finder.data.repository

import com.learnkt.kurdish_satalite_finder.data.local.dao.SatelliteDao
import com.learnkt.kurdish_satalite_finder.data.local.entity.SatelliteEntity
import com.learnkt.kurdish_satalite_finder.data.mapper.toSatellite
import com.learnkt.kurdish_satalite_finder.data.mapper.toSatelliteEntity
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SatelliteRepositoryImpl @Inject constructor(
    private val dao: SatelliteDao
) : SatelliteRepository {

    override fun getAllSatellites(): Flow<List<Satellite>> {
        return dao.getAllSatellites().map { entities ->
            entities.map { it.toSatellite() }
        }
    }

    override fun getFavoriteSatellites(): Flow<List<Satellite>> {
        return dao.getFavoriteSatellites().map { entities ->
            entities.map { it.toSatellite() }
        }
    }

    override suspend fun toggleFavorite(satellite: Satellite) {
        dao.updateSatellite(satellite.toSatelliteEntity().copy(isFavorite = !satellite.isFavorite))
    }

    override fun searchSatellites(query: String): Flow<List<Satellite>> {
        return dao.searchSatellites(query).map { entities ->
            entities.map { it.toSatellite() }
        }
    }

    override suspend fun seedDatabase() {
        val initialSatellites = listOf(
            SatelliteEntity(name = "Türksat 42E", longitude = 42.0),
            SatelliteEntity(name = "Nilesat 7W", longitude = -7.0),
            SatelliteEntity(name = "Hotbird 13E", longitude = 13.0),
            SatelliteEntity(name = "Astra 19E", longitude = 19.2),
            SatelliteEntity(name = "Eutelsat 7E", longitude = 7.0),
            SatelliteEntity(name = "Amos 4W", longitude = -4.0),
            SatelliteEntity(name = "Badr 26E", longitude = 26.0),
            SatelliteEntity(name = "Yahsat 52E", longitude = 52.5)
        )
        dao.insertSatellites(initialSatellites)
    }
}
