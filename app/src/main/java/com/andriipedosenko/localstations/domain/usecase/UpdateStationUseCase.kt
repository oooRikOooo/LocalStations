package com.andriipedosenko.localstations.domain.usecase

import android.util.Log
import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.domain.repository.StationRepository
import javax.inject.Inject

class UpdateStationUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(
        mcc: String,
        mnc: String,
        lac: String,
        cellId: Long,
        psc: String,
        rat: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        val station = Station(
            mcc = mcc.toInt(),
            mnc = mnc.toInt(),
            lac = lac.toInt(),
            cellId = cellId,
            psc = psc.toInt(),
            rat = rat,
            latitude = latitude,
            longitude = longitude
        )

        return try {
            val result = stationRepository.updateStation(station)

            if (result == 0) Result.failure(Throwable())
            else Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}