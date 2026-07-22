package com.learnkt.kurdish_satalite_finder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "satellites",
    indices = [androidx.room.Index(value = ["name"], unique = true)]
)
data class SatelliteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val longitude: Double,
    val isFavorite: Boolean = false
)
