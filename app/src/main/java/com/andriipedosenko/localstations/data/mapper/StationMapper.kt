package com.andriipedosenko.localstations.data.mapper

import com.andriipedosenko.localstations.data.local.entity.StationEntity
import com.andriipedosenko.localstations.domain.model.Station
import javax.inject.Inject

class StationMapper @Inject constructor() {
    fun toEntity(model: Station): StationEntity {
        return StationEntity(
            mcc = model.mcc,
            mnc = model.mnc,
            lac = model.lac,
            cellId = model.cellId,
            psc = model.psc,
            rat = model.rat,
            latitude = model.latitude,
            longitude = model.longitude
        )
    }

    fun toModel(entity: StationEntity): Station {
        return Station(
            mcc = entity.mcc,
            mnc = entity.mnc,
            lac = entity.lac,
            cellId = entity.cellId,
            psc = entity.psc,
            rat = entity.rat,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }
}