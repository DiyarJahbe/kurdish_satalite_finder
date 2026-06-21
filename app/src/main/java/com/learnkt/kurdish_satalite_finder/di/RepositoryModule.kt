package com.learnkt.kurdish_satalite_finder.di

import com.learnkt.kurdish_satalite_finder.data.repository.SatelliteRepositoryImpl
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSatelliteRepository(
        satelliteRepositoryImpl: SatelliteRepositoryImpl
    ): SatelliteRepository
}
