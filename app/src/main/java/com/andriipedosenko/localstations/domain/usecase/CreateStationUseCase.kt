package com.andriipedosenko.localstations.domain.usecase

import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.domain.repository.StationRepository
import javax.inject.Inject
import kotlin.random.Random

class CreateStationUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(
        mcc: String,
        mnc: String,
        lac: String,
        psc: String,
        rat: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return try {
            val station = Station(
                mcc = mcc.toInt(),
                mnc = mnc.toInt(),
                lac = lac.toInt(),
                cellId = Random.nextLong(0, 10000000),
                psc = psc.toInt(),
                rat = rat,
                latitude = latitude,
                longitude = longitude
            )

            val result = stationRepository.insertStation(station)

            if (result == -1L) Result.failure(Throwable())
            else Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}