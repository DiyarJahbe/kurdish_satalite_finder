package com.learnkt.kurdish_satalite_finder.domain.usecase

import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSatellitesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    operator fun invoke(): Flow<List<Satellite>> {
        return repository.getAllSatellites()
    }
}
