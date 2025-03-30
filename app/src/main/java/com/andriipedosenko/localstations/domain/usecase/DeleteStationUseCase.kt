package com.andriipedosenko.localstations.domain.usecase

import com.andriipedosenko.localstations.domain.repository.StationRepository
import javax.inject.Inject

class DeleteStationUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(id: Long): Result<Unit> {
        return try {
            val result = stationRepository.deleteStation(id)

            if (result == 0) Result.failure(Throwable())
            else Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}