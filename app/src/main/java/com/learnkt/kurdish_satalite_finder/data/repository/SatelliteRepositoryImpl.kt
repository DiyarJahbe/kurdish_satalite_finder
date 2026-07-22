package com.learnkt.kurdish_satalite_finder.data.repository

import com.learnkt.kurdish_satalite_finder.data.local.dao.SatelliteDao
import com.learnkt.kurdish_satalite_finder.data.local.entity.SatelliteEntity
import com.learnkt.kurdish_satalite_finder.data.mapper.toSatellite
import com.learnkt.kurdish_satalite_finder.data.mapper.toSatelliteEntity
import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun getSatelliteById(id: Int): Satellite? {
        return dao.getSatelliteById(id)?.toSatellite()
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
        val existingSatellites = dao.getAllSatellites().first()
        if (existingSatellites.isNotEmpty()) {
            return
        }

        val initialSatellites = listOf(
            // Middle East & North Africa (popular in Kurdish region)
            SatelliteEntity(name = "Nilesat 201", longitude = -7.0),
            SatelliteEntity(name = "Nilesat 301", longitude = -7.0),
            SatelliteEntity(name = "Türksat 3A", longitude = 42.0),
            SatelliteEntity(name = "Türksat 4A", longitude = 42.0),
            SatelliteEntity(name = "Türksat 5A", longitude = 42.0),
            SatelliteEntity(name = "Hotbird 13B", longitude = 13.0),
            SatelliteEntity(name = "Hotbird 13C", longitude = 13.0),
            SatelliteEntity(name = "Hotbird 13E", longitude = 13.0),
            SatelliteEntity(name = "Eutelsat 7B", longitude = 7.0),
            SatelliteEntity(name = "Eutelsat 7C", longitude = 7.0),
            SatelliteEntity(name = "Eutelsat 8 West B", longitude = -8.0),
            SatelliteEntity(name = "Arabsat 5A", longitude = 30.5),
            SatelliteEntity(name = "Arabsat 6B", longitude = 26.0),
            SatelliteEntity(name = "Badr 4", longitude = 26.0),
            SatelliteEntity(name = "Badr 5", longitude = 26.0),
            SatelliteEntity(name = "Badr 6", longitude = 26.0),
            SatelliteEntity(name = "Badr 7", longitude = 26.0),
            SatelliteEntity(name = "Yahsat 1A", longitude = 52.5),
            SatelliteEntity(name = "Yahsat 2", longitude = 52.5),
            SatelliteEntity(name = "Es'hail 1", longitude = 25.5),
            SatelliteEntity(name = "Es'hail 2", longitude = 26.0),

            // European satellites
            SatelliteEntity(name = "Astra 1KR", longitude = 19.2),
            SatelliteEntity(name = "Astra 1L", longitude = 19.2),
            SatelliteEntity(name = "Astra 1M", longitude = 19.2),
            SatelliteEntity(name = "Astra 1N", longitude = 19.2),
            SatelliteEntity(name = "Astra 2G", longitude = 28.2),
            SatelliteEntity(name = "Astra 2F", longitude = 28.2),
            SatelliteEntity(name = "Eutelsat 5 West A", longitude = -5.0),
            SatelliteEntity(name = "Eutelsat 10A", longitude = 10.0),
            SatelliteEntity(name = "Eutelsat 16A", longitude = 16.0),
            SatelliteEntity(name = "Eutelsat 21B", longitude = 21.6),
            SatelliteEntity(name = "Hispasat 30W-4", longitude = -30.0),
            SatelliteEntity(name = "Hispasat 36W-1", longitude = -36.0),

            // Asian satellites
            SatelliteEntity(name = "AsiaSat 5", longitude = 100.5),
            SatelliteEntity(name = "AsiaSat 7", longitude = 105.5),
            SatelliteEntity(name = "Thaicom 5", longitude = 78.5),
            SatelliteEntity(name = "Thaicom 8", longitude = 78.5),
            SatelliteEntity(name = "Express AM6", longitude = 53.0),
            SatelliteEntity(name = "Express AMU1", longitude = 36.0),
            SatelliteEntity(name = "Intelsat 902", longitude = 62.0),
            SatelliteEntity(name = "Intelsat 10-02", longitude = 1.0),
            SatelliteEntity(name = "Intelsat 17", longitude = 66.0),
            SatelliteEntity(name = "Intelsat 20", longitude = 68.5),

            // Other regional satellites
            SatelliteEntity(name = "Amos 3", longitude = -4.0),
            SatelliteEntity(name = "Amos 17", longitude = -17.0),
            SatelliteEntity(name = "G-Sat 17", longitude = 96.5),
            SatelliteEntity(name = "G-Sat 30", longitude = 48.0),
            SatelliteEntity(name = "Telstar 12 VANTAGE", longitude = -15.0),
            SatelliteEntity(name = "SES-6", longitude = -40.5),
            SatelliteEntity(name = "SES-10", longitude = -67.0),
            SatelliteEntity(name = "SES-14", longitude = -47.5),
            SatelliteEntity(name = "NSS-12", longitude = 57.0),
            SatelliteEntity(name = "ABS-2", longitude = 75.0),

            // Additional Middle East coverage
            SatelliteEntity(name = "Noorsat 1", longitude = 25.9),
            SatelliteEntity(name = "Noorsat 2B", longitude = 25.9),
            SatelliteEntity(name = "OmanSat", longitude = 49.0),
            SatelliteEntity(name = "Qatar-1", longitude = 26.0),
            SatelliteEntity(name = "SaudiSat 5", longitude = 30.0),
            SatelliteEntity(name = "KuwaitSat 1", longitude = 28.0),
            SatelliteEntity(name = "IranSat 1", longitude = 26.0),
            SatelliteEntity(name = "AfghanSat 1", longitude = 48.0)
        )
        dao.insertSatellites(initialSatellites)
    }
}
