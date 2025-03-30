package com.andriipedosenko.localstations.domain.repository

import com.andriipedosenko.localstations.domain.model.Station
import com.google.android.gms.maps.model.LatLngBounds

interface StationRepository {
    fun insertStation(station: Station): Long
    fun updateStation(station: Station): Int
    fun deleteStation(id: Long): Int
    suspend fun getStationsWithinBounds(bounds: LatLngBounds, limit: Int): List<Station>
}