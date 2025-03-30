package com.andriipedosenko.localstations.domain.usecase

import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.domain.repository.StationRepository
import com.google.android.gms.maps.model.LatLngBounds
import javax.inject.Inject

class GetStationInRangeUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    suspend operator fun invoke(
        visibleCoordinates: LatLngBounds,
        limit: Int
    ): Result<List<Station>> {

        return try {
            val result = stationRepository.getStationsWithinBounds(
                bounds = visibleCoordinates,
                limit = limit
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}