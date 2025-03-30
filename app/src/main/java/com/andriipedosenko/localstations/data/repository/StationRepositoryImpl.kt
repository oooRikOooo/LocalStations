package com.andriipedosenko.localstations.data.repository

import com.andriipedosenko.localstations.data.local.dao.StationDao
import com.andriipedosenko.localstations.data.mapper.StationMapper
import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.domain.repository.StationRepository
import com.google.android.gms.maps.model.LatLngBounds
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val dao: StationDao,
    private val mapper: StationMapper
) : StationRepository {
    override fun insertStation(station: Station): Long {
        val entity = mapper.toEntity(station)

        return dao.insertStation(entity)
    }

    override fun updateStation(station: Station): Int {
        val entity = mapper.toEntity(station)

        return dao.updateStation(entity)
    }

    override fun deleteStation(id: Long): Int {
        return dao.deleteStation(id)
    }

    override suspend fun getStationsWithinBounds(
        bounds: LatLngBounds,
        limit: Int
    ): List<Station> {
        val entityList = dao.getStationsWithinBounds(bounds, limit)

        return entityList.map { mapper.toModel(it) }
    }
}