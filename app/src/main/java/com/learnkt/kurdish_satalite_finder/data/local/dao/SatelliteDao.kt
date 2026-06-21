package com.learnkt.kurdish_satalite_finder.data.local.dao

import androidx.room.*
import com.learnkt.kurdish_satalite_finder.data.local.entity.SatelliteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SatelliteDao {
    @Query("SELECT * FROM satellites")
    fun getAllSatellites(): Flow<List<SatelliteEntity>>

    @Query("SELECT * FROM satellites WHERE isFavorite = 1")
    fun getFavoriteSatellites(): Flow<List<SatelliteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSatellites(satellites: List<SatelliteEntity>)

    @Update
    suspend fun updateSatellite(satellite: SatelliteEntity)

    @Query("SELECT * FROM satellites WHERE name LIKE '%' || :query || '%'")
    fun searchSatellites(query: String): Flow<List<SatelliteEntity>>
}
