package com.learnkt.kurdish_satalite_finder.domain.usecase

import com.learnkt.kurdish_satalite_finder.domain.model.Satellite
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetSatelliteByIdUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(id: Int): Satellite? {
        return repository.getSatelliteById(id)
    }
}
