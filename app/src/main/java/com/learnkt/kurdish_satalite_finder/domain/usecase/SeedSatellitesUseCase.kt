package com.learnkt.kurdish_satalite_finder.domain.usecase

import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import javax.inject.Inject

class SeedSatellitesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke() {
        repository.seedDatabase()
    }
}
