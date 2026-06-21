package com.learnkt.kurdish_satalite_finder.domain.repository

import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import kotlinx.coroutines.flow.Flow

interface SatelliteRepository {
    fun getAllSatellites(): Flow<List<Satellite>>
    fun getFavoriteSatellites(): Flow<List<Satellite>>
    suspend fun toggleFavorite(satellite: Satellite)
    fun searchSatellites(query: String): Flow<List<Satellite>>
    suspend fun seedDatabase()
}
