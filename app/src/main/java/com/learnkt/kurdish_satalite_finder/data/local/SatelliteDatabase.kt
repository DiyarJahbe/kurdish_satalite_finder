package com.learnkt.kurdish_satalite_finder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learnkt.kurdish_satalite_finder.data.local.dao.SatelliteDao
import com.learnkt.kurdish_satalite_finder.data.local.entity.SatelliteEntity

@Database(entities = [SatelliteEntity::class], version = 1, exportSchema = false)
abstract class SatelliteDatabase : RoomDatabase() {
    abstract val satelliteDao: SatelliteDao

    companion object {
        const val DATABASE_NAME = "satellite_db"
    }
}
